package ar.unrn.main;

import java.util.Arrays;

import ar.unrn.tp.cassandra.CassandraDB;
import ar.unrn.tp.web.WebAPI;

public class Main {

	public static void main(String[] args) {
		// Crear una instancia de la conexión a Cassandra
		CassandraDB cassandra = new CassandraDB();
		try {
			// Conexión a Cassandra
			cassandra.connect("127.0.0.1", 9042, "blog");

			// Insertar algunos posts de ejemplo
			cassandra.insertPost("Café", "Sobre el Café solo...", "El texto completo del post...",
					Arrays.asList("cafe", "infusion"), Arrays.asList("http://cafenegro.com", "http://cafecito.com"),
					"Jorge Boles");

			cassandra.insertPost("Té", "Sobre el Té solo...", "El texto completo del post...",
					Arrays.asList("te", "infusion"), Arrays.asList("http://te.com", "http://teconleche.com"),
					"Jorge Boles");

			cassandra.insertPost("Mate", "Sobre el mate...", "Un artículo completo sobre el mate...",
					Arrays.asList("mate", "infusion"), Arrays.asList("http://mate.com", "http://terere.com"),
					"Ana López");

			cassandra.insertPost("Chocolate", "Todo sobre el chocolate...", "El texto sobre chocolate...",
					Arrays.asList("chocolate", "dulces"), Arrays.asList("http://chocolate.com", "http://cacao.com"),
					"Jorge Boles");
			cassandra.insertPost("Helados", "Variedades de helados...", "Un artículo completo sobre helados...",
					Arrays.asList("helado", "dulces", "postres"),
					Arrays.asList("http://helados.com", "http://saborhelado.com"), "Carla Domínguez");

			cassandra.insertPost("Cerveza", "Historia de la cerveza...", "El texto completo sobre cerveza...",
					Arrays.asList("cerveza", "bebidas", "alcohol"),
					Arrays.asList("http://cervezaartesanal.com", "http://lager.com"), "Carla Domínguez");

			cassandra.insertPost("Vino", "El arte del vino...", "Todo lo que necesitas saber sobre vinos...",
					Arrays.asList("vino", "bebidas", "alcohol"),
					Arrays.asList("http://vinotinto.com", "http://vinoblanco.com"), "Laura Fernández");

			cassandra.insertPost("Pizza", "Orígenes de la pizza...",
					"Un artículo sobre las mejores pizzas del mundo...", Arrays.asList("pizza", "comida", "italiana"),
					Arrays.asList("http://pizza.com", "http://pizzanapolitana.com"), "Gonzalo Pérez");

			cassandra.insertPost("Empanadas", "Historia de las empanadas...",
					"Descubre diferentes tipos de empanadas...", Arrays.asList("empanadas", "comida", "tradicional"),
					Arrays.asList("http://empanadas.com", "http://empanadasargentinas.com"), "Ana López");

			cassandra.insertPost("Tecnología", "Últimos avances en tecnología...",
					"Explora los desarrollos más recientes...", Arrays.asList("tecnología", "innovación", "gadgets"),
					Arrays.asList("http://technews.com", "http://gadgets.com"), "Julio Mark");

			cassandra.insertPost("Fotografía", "Consejos de fotografía...", "Mejora tus habilidades fotográficas...",
					Arrays.asList("fotografía", "arte", "cámaras"),
					Arrays.asList("http://fototips.com", "http://camara.com"), "Carla Domínguez");

			cassandra.insertPost("Jardinería", "Guía para principiantes...", "Cómo empezar con la jardinería...",
					Arrays.asList("jardinería", "plantas", "hogar"),
					Arrays.asList("http://jardinería.com", "http://plantas.com"), "Marcos Ruiz");

			cassandra.insertPost("Programación", "Consejos para aprender a programar...",
					"Aprende sobre los fundamentos de programación...",
					Arrays.asList("programación", "tecnología", "educación"),
					Arrays.asList("http://programacion.com", "http://códigofuente.com"), "Laura Fernández");

			cassandra.insertPost("Viajes", "Destinos exóticos...",
					"Explora lugares increíbles para tus próximas vacaciones...",
					Arrays.asList("viajes", "aventura", "turismo"),
					Arrays.asList("http://viajes.com", "http://destinos.com"), "Gonzalo Pérez");

			// Insertar algunas páginas de ejemplo
			// cassandra.insertPage("Historia del Café", "Un artículo detallado sobre la
			// historia del café...",
			// "Luis Perez", Instant.now());
			// cassandra.insertPage("Tipos de Té", "Descripción de los diferentes tipos de
			// té en el mundo...",
			// "Maria Gomez", Instant.now());

			// Iniciar la API web
			WebAPI servicio = new WebAPI(1234, cassandra);
			servicio.start(); // Iniciar el servidor web
			System.err.println("Servicio iniciado correctamente.");
		} catch (Exception e) {
			// Si ocurre un error, mostrarlo en consola
			System.err.println("Error al iniciar el servicio: " + e.getMessage());
			throw new RuntimeException("Error al iniciar el servicio", e);
		} finally {
			// Asegurarse de desconectar la conexión a Cassandra cuando se termine el
			// proceso
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				cassandra.disconnect();
				System.out.println("Desconexión de Cassandra finalizada.");
			}));
		}
	}
}
