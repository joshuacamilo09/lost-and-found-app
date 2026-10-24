package com.lostandfound.service.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageService {

    private final Cloudinary cloudinary;

    public ImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload de imagem para Cloudinary
     * @param file - ficheiro da imagem
     * @return URL pública da imagem
     */
    public String uploadImage(MultipartFile file) {
        try {
            // Validar se o ficheiro é uma imagem
            if (file.isEmpty()) {
                throw new RuntimeException("Ficheiro vazio");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Ficheiro não é uma imagem válida");
            }

            // Gerar nome único para a imagem
            String publicId = "lost-and-found/" + UUID.randomUUID().toString();

            // Upload para Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", "lost-and-found",
                            "resource_type", "image",
                            "transformation", ObjectUtils.asMap(
                                    "width", 800,
                                    "height", 800,
                                    "crop", "limit",
                                    "quality", "auto"
                            )
                    ));

            // Retornar URL segura da imagem
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload da imagem: " + e.getMessage());
        }
    }

    /**
     * Apagar imagem do Cloudinary
     * @param imageUrl - URL da imagem a apagar
     */
    public void deleteImage(String imageUrl) {
        try {
            // Extrair public_id da URL
            String publicId = extractPublicIdFromUrl(imageUrl);

            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao apagar imagem: " + e.getMessage());
        }
    }

    /**
     * Extrair public_id da URL do Cloudinary
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return null;
        }

        // URL exemplo: https://res.cloudinary.com/cloud-name/image/upload/v123/lost-and-found/uuid.jpg
        String[] parts = imageUrl.split("/upload/");
        if (parts.length < 2) {
            return null;
        }

        // Obter a parte após "/upload/"
        String afterUpload = parts[1];
        
        // Remover versão (v123/) se existir
        if (afterUpload.matches("v\\d+/.*")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
        }

        // Remover extensão (.jpg, .png, etc)
        return afterUpload.substring(0, afterUpload.lastIndexOf("."));
    }
}