// import org.springframework.context.annotation.Configuration;
// import
// org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// import java.nio.file.Path;
// import java.nio.file.Paths;

// @Configuration
// public class WebConfig implements WebMvcConfigurer {

// @Override
// public void addResourceHandlers(ResourceHandlerRegistry registry) {
// // 指向 uploads 的父層資料夾
// String path =
// Paths.get("src/main/resources/static/uploads").toAbsolutePath().toUri().toString();

// registry.addResourceHandler("/uploads/**")
// .addResourceLocations(path);

// System.out.println("🚀 資源映射起點: " + path);
// }
// }