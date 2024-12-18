package ar.unrn.main;

import java.util.Arrays;

import ar.unrn.tp.cassandra.CassandraDB;
import ar.unrn.tp.web.WebAPI;

public class Main {

	public static void main(String[] args) {
		// Crear una instancia de la conexi�n a Cassandra
		CassandraDB cassandra = new CassandraDB();
		try {
			// Conexi�n a Cassandra
			cassandra.connect("127.0.0.1", 9042, "blog");

			// Insertar algunos posts de ejemplo
			cassandra.insertPost("Caf�", "Sobre el Caf� solo...", "El texto completo del post...",
					Arrays.asList("cafe", "infusion"), Arrays.asList("http://cafenegro.com", "http://cafecito.com"),
					"Jorge Boles");

			cassandra.insertPost("T�", "Sobre el T� solo...", "El texto completo del post...",
					Arrays.asList("te", "infusion"), Arrays.asList("http://te.com", "http://teconleche.com"),
					"Jorge Boles");

			cassandra.insertPost("Mate", "Sobre el mate...", "Un art�culo completo sobre el mate...",
					Arrays.asList("mate", "infusion"), Arrays.asList("http://mate.com", "http://terere.com"),
					"Ana L�pez");

			cassandra.insertPost("Chocolate", "Todo sobre el chocolate...", "El texto sobre chocolate...",
					Arrays.asList("chocolate", "dulces"), Arrays.asList("http://chocolate.com", "http://cacao.com"),
					"Jorge Boles");
			cassandra.insertPost("Helados", "Variedades de helados...", "Un art�culo completo sobre helados...",
					Arrays.asList("helado", "dulces", "postres"),
					Arrays.asList("http://helados.com", "http://saborhelado.com"), "Carla Dom�nguez");

			cassandra.insertPost("Cerveza", "Historia de la cerveza...", "El texto completo sobre cerveza...",
					Arrays.asList("cerveza", "bebidas", "alcohol"),
					Arrays.asList("http://cervezaartesanal.com", "http://lager.com"), "Carla Dom�nguez");

			cassandra.insertPost("Vino", "El arte del vino...", "Todo lo que necesitas saber sobre vinos...",
					Arrays.asList("vino", "bebidas", "alcohol"),
					Arrays.asList("http://vinotinto.com", "http://vinoblanco.com"), "Laura Fern�ndez");

			cassandra.insertPost("Pizza", "Or�genes de la pizza...",
					"Un art�culo sobre las mejores pizzas del mundo...", Arrays.asList("pizza", "comida", "italiana"),
					Arrays.asList("http://pizza.com", "http://pizzanapolitana.com"), "Gonzalo P�rez");

			cassandra.insertPost("Empanadas", "Historia de las empanadas...",
					"Descubre diferentes tipos de empanadas...", Arrays.asList("empanadas", "comida", "tradicional"),
					Arrays.asList("http://empanadas.com", "http://empanadasargentinas.com"), "Ana L�pez");

			cassandra.insertPost("Tecnolog�a", "�ltimos avances en tecnolog�a...",
					"Explora los desarrollos m�s recientes...", Arrays.asList("tecnolog�a", "innovaci�n", "gadgets"),
					Arrays.asList("http://technews.com", "http://gadgets.com"), "Julio Mark");

			cassandra.insertPost("Fotograf�a", "Consejos de fotograf�a...", "Mejora tus habilidades fotogr�ficas...",
					Arrays.asList("fotograf�a", "arte", "c�maras"),
					Arrays.asList("http://fototips.com", "http://camara.com"), "Carla Dom�nguez");

			cassandra.insertPost("Jardiner�a", "Gu�a para principiantes...", "C�mo empezar con la jardiner�a...",
					Arrays.asList("jardiner�a", "plantas", "hogar"),
					Arrays.asList("http://jardiner�a.com", "http://plantas.com"), "Marcos Ruiz");

			cassandra.insertPost("Programaci�n", "Consejos para aprender a programar...",
					"Aprende sobre los fundamentos de programaci�n...",
					Arrays.asList("programaci�n", "tecnolog�a", "educaci�n"),
					Arrays.asList("http://programacion.com", "http://c�digofuente.com"), "Laura Fern�ndez");

			cassandra.insertPost("Viajes", "Destinos ex�ticos...",
					"Explora lugares incre�bles para tus pr�ximas vacaciones...",
					Arrays.asList("viajes", "aventura", "turismo"),
					Arrays.asList("http://viajes.com", "http://destinos.com"), "Gonzalo P�rez");

			// Insertar algunas p�ginas de ejemplo
			// cassandra.insertPage("Historia del Caf�", "Un art�culo detallado sobre la
			// historia del caf�...",
			// "Luis Perez", Instant.now());
			// cassandra.insertPage("Tipos de T�", "Descripci�n de los diferentes tipos de
			// t� en el mundo...",
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
			// Asegurarse de desconectar la conexi�n a Cassandra cuando se termine el
			// proceso
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				cassandra.disconnect();
				System.out.println("Desconexi�n de Cassandra finalizada.");
			}));
		}
	}
}
