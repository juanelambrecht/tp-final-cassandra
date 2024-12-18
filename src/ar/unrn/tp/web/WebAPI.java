package ar.unrn.tp.web;

import java.util.Map;

import io.javalin.Javalin;
import io.javalin.http.Handler;

import ar.unrn.tp.cassandra.CassandraDB;

public class WebAPI {

    private int puerto;
    private CassandraDB cassandra;

    public WebAPI(int puerto, CassandraDB cassandra) {
        this.puerto = puerto;
        this.cassandra = cassandra;
    }

    public void start() {
        Javalin javalin = Javalin.create(config -> {
            config.enableCorsForAllOrigins();
        }).start(this.puerto);

        // Rutas
        javalin.get("/posts", allPosts());
        javalin.get("/pages/{id}", pages());
        javalin.get("/posts/latest",latest());
        javalin.get("/posts/{id}", postById());
        javalin.get("/posts/author/{name}", postsByAuthor());
        javalin.get("/byauthor", byauthor());
        javalin.post("/posts", createPost());
        javalin.get("/search/{text}", search());

        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.json(Map.of("result", "error", "message", "Algo salió mal: " + e.getMessage())).status(400);
        });
    }

    private Handler allPosts() {
        return ctx -> {
            var posts = this.cassandra.findAllPosts();
            ctx.json(posts);
        };
    }
    
	private Handler pages() {
		return ctx -> {
			var id = String.valueOf(ctx.pathParam("id"));
			var pages = this.cassandra.findAllPages(id);

			ctx.json(pages);
		};
	}

    private Handler postById() {
        return ctx -> {
            var id = String.valueOf(ctx.pathParam("id"));
            var post = this.cassandra.findPostById(id);
            if (post.isEmpty()) {
                ctx.status(404).json(Map.of("message", "Post no encontrado"));
            } else {
                ctx.json(post);
            }
        };
    }

    private Handler postsByAuthor() {
        return ctx -> {
            var name = String.valueOf(ctx.pathParam("name"));
			var posts = this.cassandra.findPostsByAuthor(name);
            ctx.json(posts);
        };
    }

    private Handler createPost() {
        return ctx -> {
            var body = ctx.bodyAsClass(PostRequest.class); // Mapeo automático del JSON a una clase
            this.cassandra.insertPost(body.title, body.resume, body.text, body.tags, body.relatedLinks, body.author);
            ctx.status(201).json(Map.of("message", "Post creado exitosamente"));
        };
    }
    
    private Handler latest() {
    	 return ctx -> {
             var posts = this.cassandra.findPostLatest();
             ctx.json(posts);
        };
    }

	private Handler byauthor() {
		return ctx -> {
			var byauthor = this.cassandra.findByAuthor();

			ctx.json(byauthor);
		};
	}
	
	private Handler search() {
		return ctx -> {
			var text = String.valueOf(ctx.pathParam("text"));
			var resultSearch = this.cassandra.findSearch(text);

			ctx.json(resultSearch);
		};
	}
	
    // Clase auxiliar para parsear el cuerpo de los requests
    public static class PostRequest {
        public String title;
        public String resume;
        public String text;
        public java.util.List<String> tags;
        public java.util.List<String> relatedLinks;
        public String author;
    }
}
