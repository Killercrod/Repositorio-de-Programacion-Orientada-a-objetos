package bd;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class Database {
    private Database() {}

    public static Connection getConnection() throws SQLException {
        String[] creds;
        try {
            creds = loadCredentials();
        } catch (IOException e) {
            throw new SQLException("Failed to load DB credentials from credenciales.txt: " + e.getMessage(), e);
        }

        String url = creds[0];
        String user = creds[1];
        String pass = creds[2];

        return DriverManager.getConnection(url, user, pass);
    }

    private static String[] loadCredentials() throws IOException {
        List<String> lines = null;

        try (InputStream is = Database.class.getResourceAsStream("credenciales.txt")) {
            if (is != null) {
                lines = readLinesFromStream(is);
            }
        }

        if (lines == null) {
            Path p = Path.of("src", "main", "java", "bd", "credenciales.txt");
            if (Files.exists(p)) {
                lines = Files.readAllLines(p, StandardCharsets.UTF_8);
            }
        }

        if (lines == null) {
            throw new IOException("credenciales.txt not found on classpath or at src/main/java/bd/credenciales.txt");
        }

        List<String> nonEmpty = new ArrayList<>();
        for (String l : lines) {
            if (l != null) {
                String t = l.trim();
                if (!t.isEmpty()) nonEmpty.add(t);
            }
        }
        if (nonEmpty.size() < 3) {
            throw new IOException("credenciales.txt must contain at least 3 non-empty lines: jdbcUrl, user, password");
        }

        String jdbc = nonEmpty.get(0);
        String user = nonEmpty.get(1);
        String pass = nonEmpty.get(2);
        return new String[]{jdbc, user, pass};
    }

    private static List<String> readLinesFromStream(InputStream in) throws IOException {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(in, StandardCharsets.UTF_8))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        }
    }
}
