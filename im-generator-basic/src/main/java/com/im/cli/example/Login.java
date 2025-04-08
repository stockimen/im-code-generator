package com.im.cli.example;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class Login implements Callable<Integer> {
    @Option(names = {"-u", "--user"}, description = "User name")
    String user;

    // 设置了 arity 参数，可选交互式
    @Option(names = {"-p", "--password"}, arity = "0..1", description = "Passphrase", interactive = true)
    String password;

    // 设置了 arity 参数，可选交互式
    @Option(names = {"-cp", "--checkPassword"}, arity = "0..1", description = "Check Password", interactive = true)
    String checkPassword;

    public Integer call() {
        System.out.println("User: " + user);
        // 检查密码和确认密码是否一致
        while (!isPasswordMatch()) {
            System.out.println("Passwords do not match. Please try again.");
            promptForPasswords(); // 重新输入密码和确认密码
        }
        System.out.println("password = " + password);
        System.out.println("checkPassword = " + checkPassword);
        return 0;
    }

    public static void main(String[] args) {
        args = addMissingInteractiveOptions(Login.class, args);
        new CommandLine(new Login()).execute(args);
    }

    /**
     * 自动补充缺失的交互式选项
     *
     * @param clazz 目标类
     * @param args  原始命令行参数
     * @return 补充后的命令行参数
     */
    private static String[] addMissingInteractiveOptions(Class<?> clazz, String[] args) {
        List<String> argsList = new ArrayList<>(Arrays.asList(args));

        // 通过反射获取所有 @Option 注解的字段
        for (Field field : clazz.getDeclaredFields()) {
            Option option = field.getAnnotation(Option.class);
            if (option != null && option.interactive()) {
                // 检查 args 中是否包含该选项
                boolean found = false;
                for (String arg : argsList) {
                    if (Arrays.asList(option.names()).contains(arg)) {
                        found = true;
                        break;
                    }
                }

                // 如果未找到，则补充该选项
                if (!found) {
                    argsList.add(option.names()[0]); // 使用第一个选项名称
                }
            }
        }

        return argsList.toArray(new String[0]);
    }

    /**
     * 检查密码和确认密码是否一致
     *
     * @return 如果一致返回 true，否则返回 false
     */
    private boolean isPasswordMatch() {
        return password != null && password.equals(checkPassword);
    }

    /**
     * 提示用户输入密码和确认密码
     */
    private void promptForPasswords() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter value for --password (Passphrase): ");
        this.password = scanner.nextLine();
        System.out.print("Enter value for --checkPassword (Check Password): ");
        this.checkPassword = scanner.nextLine();
    }
}
