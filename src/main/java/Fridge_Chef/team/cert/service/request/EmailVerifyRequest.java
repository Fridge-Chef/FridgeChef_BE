package Fridge_Chef.team.cert.service.request;

public record EmailVerifyRequest (String email,int code){
    public static EmailVerifyRequest of(String email, int code) {
        return new EmailVerifyRequest(email,code);
    }
}
