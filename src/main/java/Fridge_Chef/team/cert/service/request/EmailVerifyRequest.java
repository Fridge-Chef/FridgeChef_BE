package Fridge_Chef.team.cert.service.request;

public record EmailVerifyRequest (String email,int code){
    public static EmailVerifyRequest of(String send, int code) {
        return new EmailVerifyRequest(send,code);
    }
}
