from ..core import Logger, settings
from ..security import keycloak_openid


class KeycloakService:
    def __init__(self, logger: Logger):
        self.logger = logger
        self.keycloak_openid = keycloak_openid

    def login(self, username: str, password: str):
        try:
            self.logger.debug(f"Attempting login for user: {username}")
            token_response = self.keycloak_openid.token(
                grant_type="password",
                username=username,
                password=password,
                client_id=settings.KEYCLOAK_CLIENT_ID,
                client_secret=settings.KEYCLOAK_CLIENT_SECRET,
            )
            self.logger.info(f"Login successful for user: {username}")
            return token_response
        except Exception as e:
            self.logger.error(f"Login failed for user {username}: {str(e)}")
            return None

    def get_userinfo(self, token: str):
        try:
            self.logger.debug("Fetching user info from token")
            user_info = self.keycloak_openid.userinfo(token)
            self.logger.info("User info fetched successfully")
            return user_info
        except Exception as e:
            self.logger.error(f"Error fetching user info: {str(e)}")
            raise

    def decode_token(self, token: str):
        try:
            self.logger.debug("Decoding token")
            token_info = self.keycloak_openid.decode_token(token)
            self.logger.info("Token decoded successfully")
            return token_info
        except Exception as e:
            self.logger.error(f"Error decoding token: {str(e)}")
            raise


# Dependency injection function
def get_keycloak_service(logger: Logger = Logger("KeycloakService")):
    return KeycloakService(logger)
