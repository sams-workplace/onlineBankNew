package onlinebank.external;

public class AuthServiceFallback implements AuthService{
    @Override
    public void requestAuth(Auth auth) {
        System.out.println("Circuit breaker has been opened. Fallback returned instead.");
    }
}