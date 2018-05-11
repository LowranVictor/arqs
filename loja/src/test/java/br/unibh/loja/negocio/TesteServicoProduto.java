package br.unibh.loja.negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Logger;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import br.unibh.loja.entidades.Cliente;
import br.unibh.loja.entidades.Categoria;
import br.unibh.loja.entidades.Produto;
import br.unibh.loja.util.Resources;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TesteServicoProduto {
	
	Categoria c = new Categoria(1L,"Teste");
	
	@Deployment
	public static Archive<?> createTestArchive() {
		
		// Cria o pacote que vai ser instalado no Wildfly para realizacao dos testes
		return ShrinkWrap.create(WebArchive.class, "testeseguro.war")
		.addClasses(Cliente.class, Categoria.class, Produto.class, 	Resources.class, DAO.class, ServicoProduto.class)
		.addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
		.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		
		}
	
	// Realiza as injecoes com CDI
	@Inject
	private Logger log;
	@Inject
	private ServicoProduto sp;
	
	
	
	@Test
	public void inserirSemErroProduto() throws Exception {
		
		log.info("============> Iniciando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
		
		BigDecimal a = new BigDecimal("3589.00");
		Produto p1 = new Produto(1L,"iPhone 7","Red",c, a ,"Apple");
		sp.insert(p1);
		Produto aux = (Produto) sp.findByName("iPhone 7").get(0);
		assertNotNull(aux);
		log.info("============> Finalizando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	
	@Test
	public void inserirComErroProduto() throws Exception {
		
		log.info("============> Iniciando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			BigDecimal a = new BigDecimal("3589.00");
			Produto p1 = new Produto(1L,"iPhone 7@","Red",c, a ,"Apple");
				sp.insert(p1);
		} 
		catch (Exception e){
				assertTrue(checkString(e, "Caracteres permitidos: letras, espaços, ponto e aspas simples"));
		}
		log.info("============> Finalizando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Test
	public void atualizarProduto() throws Exception {
		
		log.info("============> Iniciando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
		Produto o = (Produto) sp.findByName("iPhone 7").get(0);
		o.setNome("iPhone 7 modificado");
		sp.update(o);
		Produto aux = (Produto) sp.findByName("Lowran modificado").get(0);
		assertNotNull(aux);
		log.info("============> Finalizando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	
	@Test
	public void excluirCliente() throws Exception {
		
		log.info("============> Iniciando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
		Produto o = (Produto) sp.findByName("iPhone 7").get(0);
		sp.delete(o);
		assertEquals(0, sp.findByName("Lowran modificado").size());
		log.info("============> Finalizando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	
	private boolean checkString(Throwable e, String str){
		
		if (e.getMessage().contains(str)){
			return true;
		} 
		else if (e.getCause() != null){
			return checkString(e.getCause(), str);
		}
			return false;
		}
}
