package com.rafaelximenes.cursomc.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rafaelximenes.cursomc.services.exception.FileException;

@Service
public class ImageService {
	
	public BufferedImage getJpgImageFromFile(MultipartFile uploadedFile) {
		String ext = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
		if(!"png".equalsIgnoreCase(ext) && !"jpg".equalsIgnoreCase(ext)) {
			throw new FileException("Formato não suportado");
		}
		try {
			BufferedImage img = ImageIO.read(uploadedFile.getInputStream());
			if("png".equals(ext)) {
				img = pngToJpg(img);
			}
			return img;
		} catch (IOException e) {
			throw new FileException("Erro ao ler arquivo");
		}
	}

	private BufferedImage pngToJpg(BufferedImage img) {
		BufferedImage jpg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		jpg.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
		return jpg;
	}
	
	public InputStream getInputStream(BufferedImage img, String extesion) {
		try {
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(img, extesion, arrayOutputStream);
			return new ByteArrayInputStream(arrayOutputStream.toByteArray());
		} catch (IOException e) {
			throw new FileException("Erro ao ler arquivo");
		}
		
	}
	
	public BufferedImage cropSquare(BufferedImage img) {
		int min = (img.getHeight() <= img.getWidth()) ? img.getHeight() : img.getWidth();
		return Scalr.crop(img, (img.getWidth()/2) - (min/2) , (img.getHeight()/2) - (min/2), min, min);
	}
	
	public BufferedImage resize(BufferedImage img,  int size) {
		return Scalr.resize(img, Scalr.Method.ULTRA_QUALITY, size);
	}

}
