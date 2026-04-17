package org.example;

import java.io.File;
import java.util.Scanner;

public class CUI {
    private final Scanner scanner = new Scanner(System.in);
    private final ParserFactory factory = new ParserFactory();

    public void start() {
        while (true) {
            System.out.println("\n=== ГЛАВНОЕ МЕНЮ ===");
            System.out.println("1. Загрузить миссию из файла");
            System.out.println("2. Завершить работу");
            System.out.print("Выбор: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    loadMission();
                    break;
                case "2":
                    System.out.println("Программа завершена");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный ввод. Пожалуйста, выберите 1 или 2.\n");
            }
        }
    }

    private void loadMission() {
        System.out.print("\nВведите путь к файлу: ");
        String path = scanner.nextLine().trim();
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.out.println("Файл не найден. Проверьте правильность пути и повторите попытку.\n");
            return;
        }

        try {
            Reader reader = factory.getReader(file);
            Mission mission = reader.read(file);

            System.out.println("\nВыберите тип отчёта:");
            System.out.println("1. Краткий");
            System.out.println("2. Подробный");
            String choice = scanner.nextLine().trim();

            Summary summary;
            if ("2".equals(choice)) {
                summary = new DetailedSummary(new SimpleSummary(mission), mission);
            } else {
                summary = new SimpleSummary(mission);
            }


            System.out.println("\n" + summary.getText() + "\n");

        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка в данных файла: " + e.getMessage() + "\n");
        } catch (Exception e) {
            System.err.println("Ошибка обработки файла: " + e.getMessage());
            e.printStackTrace();
        }
    }
}