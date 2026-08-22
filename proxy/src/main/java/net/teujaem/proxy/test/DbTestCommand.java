package net.teujaem.proxy.test;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.teujaem.jpalib.database.DatabaseManager;

public class DbTestCommand
        implements SimpleCommand {

    private final DatabaseManager databaseManager;

    public DbTestCommand(
            DatabaseManager databaseManager
    ) {
        this.databaseManager =
                databaseManager;
    }

    @Override
    public void execute(
            Invocation invocation
    ) {

        String[] args =
                invocation.arguments();

        String message =
                args.length > 0
                        ? String.join(" ", args)
                        : "테스트 메시지 "
                        + System.currentTimeMillis();

        TestLog log =
                new TestLog(message);

        databaseManager
                .getJpa()
                .saveAsync(log)

                .thenAccept(saved -> {

                    invocation.source()
                            .sendMessage(
                                    Component.text(
                                            "§aDB 저장 성공! ID: "
                                                    + saved.getId()
                                    )
                            );

                })

                .exceptionally(error -> {

                    invocation.source()
                            .sendMessage(
                                    Component.text(
                                            "§cDB 저장 실패: "
                                                    + error.getMessage()
                                    )
                            );

                    return null;
                });
    }
}