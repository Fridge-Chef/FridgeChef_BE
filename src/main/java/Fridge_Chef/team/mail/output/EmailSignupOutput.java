package Fridge_Chef.team.mail.output;

public class EmailSignupOutput {
    public static String signupText(int verificationCode) {
        return """
                        안녕하세요. 
                        Fridge-Chef 를 방문해 주셔서 감사합니다. 
                        인증 번호 : [%d] 를 입력 하시면 회원가입 마무리 단계가 완료 됩니다.
                        감사합니다.



                        Home Page : 
                        DEV:https://github.com/Fridge-Chef/FridgeChef_BE
                """.formatted(verificationCode);
    }
    public static String signupTitle(){
        return "[Fridge-Chef] 메일 인증 안내드립니다.";
    }
}
