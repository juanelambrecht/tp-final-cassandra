package ar.unrn.tp.cassandra;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;

import java.net.InetSocketAddress;
import java.time.Instant;

public class CassandraDB {

	private CqlSession session;

	// Conexión a Cassandra
	public void connect(String host, int port, String keyspace) {
		try {
			session = CqlSession.builder().addContactPoint(new InetSocketAddress(host, port)).withKeyspace(keyspace)
					.withLocalDatacenter("datacenter1").build();

			session.execute("CREATE KEYSPACE IF NOT EXISTS " + keyspace
					+ " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};");

			session.execute("USE " + keyspace + ";");
			System.out.println("Conexión a Cassandra establecida correctamente.");
		} catch (Exception e) {
			System.err.println("Error al conectar a Cassandra: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Error al conectar a Cassandra", e);
		}
	}

	// Cerrar conexión
	public void disconnect() {
		try {
			if (this.session != null) {
				this.session.close();
				System.out.println("Conexión a Cassandra cerrada correctamente.");
			}
		} catch (Exception e) {
			System.err.println("Error al cerrar la conexión a Cassandra: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Error al cerrar la conexión a Cassandra", e);
		}
	}

	// Crear tabla si no existe
	public void createTable() {
		try {
			String query = """
					CREATE TABLE IF NOT EXISTS posts (
					    id UUID PRIMARY KEY,
					    title TEXT,
					    resume TEXT,
					    text TEXT,
					    tags LIST<TEXT>,
					    relatedlinks LIST<TEXT>,
					    author TEXT,
					    date TIMESTAMP
					);
					""";

			session.execute(query);
			System.out.println("Tabla 'posts' creada o ya existe.");
		} catch (Exception e) {
			System.err.println("Error al crear la tabla 'posts': " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Error al crear la tabla 'posts'", e);
		}
	}

	// Método para insertar un post
	public void insertPost(String title, String resume, String content, List<String> tags, List<String> urls,
			String author) {
		try {
			UUID id = UUID.randomUUID();
			Instant publishDate = Instant.now();

			String query = "INSERT INTO blog.posts (author, id, title, resume, text, tags, relatedlinks, publish_date) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			session.execute(query, author, id, title, resume, content, tags, urls, publishDate);

			String queryPostsByDate = "INSERT INTO blog.posts_by_date (partition_key, publish_date, id, author, title, resume) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";
			session.execute(queryPostsByDate, "latest_posts", publishDate, id, author, title, resume);

			System.out.println("Post insertado correctamente: " + title);
		} catch (Exception e) {
			System.err.println("Error al insertar el post: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Error al insertar el post", e);
		}
	}

	// Consultar todos los posts
	public String findAllPosts() {
		try {
			String query = "SELECT * FROM blog.posts;";
			ResultSet resultSet = session.execute(query);

			return StreamSupport.stream(resultSet.spliterator(), false).map(row -> rowToJson(row))
					.collect(Collectors.joining(", ", "[", "]"));
		} catch (Exception e) {
			System.err.println("Error al consultar todos los posts: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Error al consultar todos los posts", e);
		}
	}

	// Buscar un post por ID
	public String findPostById(String id) {
		try {
			String query = "SELECT * FROM posts WHERE id = ?;";
			PreparedStatement prepared = session.prepare(query);
			BoundStatement bound = prepared.bind(java.util.UUID.fromString(id));
			ResultSet resultSet = session.execute(bound);

			return StreamSupport.stream(resultSet.spliterator(), false).map(row -> rowToJson(row))
					.collect(Collectors.joining(", ", "[", "]"));
		} catch (Exception e) {
			System.err.println("Error al buscar post por ID: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Error al buscar post por ID", e);
		}
	}

	// Buscar posts por autor
	public String findPostsByAuthor(String author) {
		String query = "SELECT * FROM posts WHERE author = ? ALLOW FILTERING;";
		PreparedStatement prepared = session.prepare(query);
		BoundStatement bound = prepared.bind(author);
		ResultSet resultSet = session.execute(bound);

		return StreamSupport.stream(resultSet.spliterator(), false).map(row -> rowToJson(row))
				.collect(Collectors.joining(", ", "[", "]"));
	}

	// Consultar los últimos 4 posts
	public String findPostLatest() {
		try {
			String query = "SELECT * FROM blog.posts_by_date LIMIT 4;";
			ResultSet resultSet = session.execute(query);

			return StreamSupport.stream(resultSet.spliterator(), false).map(row -> rowToJsonForPostsByDate(row))
					.collect(Collectors.joining(", ", "[", "]"));
		} catch (Exception e) {
			System.err.println("Error al consultar los últimos posts: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Error al consultar los últimos posts", e);
		}
	}

	public String findByAuthor() {
		String query = "SELECT author FROM blog.posts";
		ResultSet resultSet = session.execute(query);

		// Contar posts agrupados por autor
		Map<String, Long> authorCounts = StreamSupport.stream(resultSet.spliterator(), false)
				.collect(Collectors.groupingBy(row -> row.getString("author"), Collectors.counting()));

		// Convertir el resultado en formato JSON compatible con React
		return authorCounts.entrySet().stream().map(entry -> String.format("""
				    {
				        "_id": "%s",
				        "count": %d
				    }
				""", entry.getKey(), entry.getValue())).collect(Collectors.joining(", ", "[", "]"));
	}

	// Método para insertar una página en la tabla pages
	public void insertPage(String title, String text, String author, Instant publishDate) {
		try {
			// Generar un UUID único para la página
			UUID id = UUID.randomUUID();

			// Crear la consulta de inserción
			String query = "INSERT INTO blog.pages (id, title, text, author, publish_date) VALUES (?, ?, ?, ?, ?)";

			// Ejecutar la consulta con los parámetros
			session.execute(query, id, title, text, author, publishDate);
			System.out.println("Página insertada correctamente: " + title);
		} catch (Exception e) {
			System.err.println("Error al insertar la página: " + e.getMessage());
		}
	}

	public String findAllPages(String id) {
		// Convertir el id a UUID
		// UUID uuid = UUID.fromString(id);

		// Consulta para obtener todas las páginas con el id proporcionado
		String query = "SELECT * FROM blog.pages";
		ResultSet resultSet = session.execute(query);

		return StreamSupport.stream(resultSet.spliterator(), false).map(row -> rowToJsonPages(row))
				.collect(Collectors.joining(", ", "[", "]"));
	}

	public String findSearch(String text) {
		// CREATE INDEX ON blog.posts (title);
		// Consulta para buscar posts que coincidan con el título
		String query = "SELECT * FROM blog.posts WHERE title = ? ALLOW FILTERING;";
		PreparedStatement prepared = session.prepare(query);
		BoundStatement bound = prepared.bind(text); // Vincular el texto buscado
		ResultSet resultSet = session.execute(bound);

		// Convertir los resultados a JSON
		return StreamSupport.stream(resultSet.spliterator(), false).map(this::rowToJson) // Convertir cada fila a JSON
																							// usando la función
																							// rowToJson
				.collect(Collectors.joining(", ", "[", "]"));
	}

	// Convertir fila a JSON
	private String rowToJson(Row row) {
		return String.format("""
				{
				    "_id": { "$oid": "%s" },
				    "author": "%s",
				    "title": "%s",
				    "resume": "%s",
				    "text": "%s",
				    "tags": [%s],
				    "relatedlinks": [%s],
				    "publish_date": "%s"
				}
				""", row.getUuid("id"), row.getString("author"), row.getString("title"), row.getString("resume"),
				row.getString("text"), formatList(row.getList("tags", String.class)),
				formatList(row.getList("relatedlinks", String.class)), row.getInstant("publish_date").toString());
	}

	private String rowToJsonForPostsByDate(Row row) {
		return String.format("""
				{
				    "_id": { "$oid": "%s" },
				    "author": "%s",
				    "title": "%s",
				    "resume": "%s",
				    "publish_date": "%s"
				}
				""", row.getUuid("id"), row.getString("author"), row.getString("title"), row.getString("resume"),
				row.getInstant("publish_date").toString());
	}

	// Convertir fila de la tabla "pages" a JSON
	private String rowToJsonPages(Row row) {
		return String.format("""
				{
				    "_id": "%s",
				    "author": "%s",
				    "title": "%s",
				    "text": "%s",
				    "publish_date": "%s"
				}
				""", row.getUuid("id"), // Campo "id" único para cada página
				row.getString("author"), row.getString("title"), row.getString("text"),
				row.getInstant("publish_date").toString()); // Convertir fecha a string ISO 8601
	}

	private String formatList(List<String> list) {
		if (list == null || list.isEmpty()) {
			return ""; // Si la lista está vacía o es nula, devolver una cadena vacía
		}
		return list.stream().map(item -> "\"" + item + "\"") // Asegurarse de que los elementos estén entre comillas
				.collect(Collectors.joining(", "));
	}

}
