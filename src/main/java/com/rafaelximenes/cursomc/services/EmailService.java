package com.rafaelximenes.cursomc.services;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;

import com.rafaelximenes.cursomc.domain.Cliente;
import com.rafaelximenes.cursomc.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendMail(SimpleMailMessage msg);
	
	void sendOrderConfirmationHtmlEmail(Pedido obj);
	
	void sendHtmlMail(MimeMessage msg);
	
	void sendNewPasswordEmail(Cliente cliente, String newPass);

}
