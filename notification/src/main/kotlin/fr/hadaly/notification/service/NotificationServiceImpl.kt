package fr.hadaly.notification.service

import arrow.core.Either
import fr.hadaly.core.model.Recommandations
import fr.hadaly.core.model.User
import fr.hadaly.core.service.Configuration
import fr.hadaly.core.service.NotificationService
import fr.hadaly.core.service.RecommendationEngine
import fr.hadaly.core.service.UserRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializer
import kotlinx.serialization.serializer
import org.slf4j.LoggerFactory
import toEmailText
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit.MONTHS
import java.time.temporal.ChronoUnit.DAYS

class NotificationServiceImpl(
    config: Configuration,
    private val userRepository: UserRepository,
    private val recommendationEngine: RecommendationEngine
) : NotificationService {
    private val logger = LoggerFactory.getLogger(NotificationServiceImpl::class.java)
    private val baseUrl = config.getString(Configuration.Name.MAIL_BASE_URL)
    private val client = HttpClient {
        serializer<Serializer>()
    }

    override suspend fun processNotifications(): Either<Throwable, Unit> = withContext(Dispatchers.IO) {
        Either.catch {
            val users = userRepository.getUsers()
            users.forEach { user ->
                user.frequency?.let {
                    val frequency = Frequency.valueOf(it.uppercase())
                    if (needsNotification(frequency, user.notifiedAt)) {
                        launch {
                            when (val recommendations = recommendationEngine.recommendFor(user.address)) {
                                is Either.Left -> {
                                    logger.error(
                                        "Fetching recommendations for ${user.address} " +
                                                "failed : ${recommendations.value.message}"
                                    )
                                }
                                is Either.Right -> {
                                    sendMail(user, recommendations.value)
                                    userRepository.updateUser(user.copy(notifiedAt = LocalDateTime.now()))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun sendMail(user: User, recommendations: Recommandations) {
        val response: HttpResponse = client.post(baseUrl) {
            parameter("from", "noreply@mg.centurio.app")
            parameter("to", user.email)
            parameter("subject", "[Centurio] You have new recommandations !")
            parameter("text", recommendations.toEmailText(user.address))
        }
        if (!response.status.isSuccess()) {
            logger.error("Sending recommendations to ${user.address} failed")
        }
    }

    private fun needsNotification(frequency: Frequency, lastNotification: LocalDateTime?): Boolean {
        return lastNotification?.let {
            val needsRefresh = when (frequency) {
                Frequency.NEVER -> false
                Frequency.DAILY -> DAYS.between(it, LocalDateTime.now()) == 1L
                Frequency.WEEKLY -> DAYS.between(it, LocalDateTime.now()) == 7L
                Frequency.MONTHLY -> MONTHS.between(it, LocalDateTime.now()) == 1L
            }
            needsRefresh
        } ?: false
    }

    enum class Frequency {
        NEVER,
        DAILY,
        WEEKLY,
        MONTHLY
    }
}
