import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static StringBuilder separator = new StringBuilder();
    public static void main(String[] args) throws IOException {
        separate();
        PracticeHTTP task1 = new PracticeHTTP();
        task1.getAllUsers();
        System.out.println(separator.toString());

        System.out.println(task1.createNewUser());
        System.out.println(separator.toString());


        System.out.println(task1.UpdateUser(5));
        System.out.println(separator.toString());


        task1.deleteUser(1);
        System.out.println(separator.toString());


        task1.getUserById(6);
        System.out.println(separator.toString());


        task1.getUserByName("Leopoldo_Corkery");
        System.out.println(separator.toString());


        PracticeHTTP task2 = new PracticeHTTP();
        task2.getCommentsForLastPost(5);
        System.out.println(separator.toString());


        PracticeHTTP task3 = new PracticeHTTP();
        task3.getTasks(2);
        System.out.println(separator.toString());

    }


    private static void separate(){

        for(int i=0;i<150;i++){
            separator.append("*");
        }
        separator.append("\n");
    }
}