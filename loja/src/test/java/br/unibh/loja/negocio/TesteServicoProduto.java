package br.unibh.loja.negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
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

import br.unibh.loja.entidades.Categoria;
import br.unibh.loja.entidades.Cliente;
import br.unibh.loja.entidades.Produto;
import br.unibh.loja.util.Resources;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TesteServicoProduto {
	
	//Categoria c = new Categoria();
	
	@Deployment
	public static Archive<?> createTestArchive() {
		
		// Cria o pacote que vai ser instalado no Wildfly para realizacao dos testes
		return ShrinkWrap.create(WebArchive.class, "testeseguro.war")
		.addClasses(Cliente.class, Categoria.class, Produto.class, 	Resources.class, DAO.class, ServicoProduto.class,ServicoCategoria.class)
		.addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
		.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		
		}
	
	// Realiza as injecoes com CDI
	@Inject
	private Logger log;
	
	@Inject
	private ServicoProduto sp;
	
	@Inject
	private ServicoCategoria sc;
	
	@Test
	public void teste01_inserirSemErroCategoria_1() throws Exception {
		
		log.info("============> Iniciando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
		
		Categoria c1 = new Categoria(1L,"Telefone");
		sc.insert(c1);
		Categoria aux = (Categoria) sc.findByName("Telefone").get(0);
		assertNotNull(aux);
		log.info("============> Finalizando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	
	@Test
	public void teste02_inserirSemErroProduto() throws Exception {
		
		log.info("============> Iniciando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
		Categoria c = (Categoria) sc.findByName("Telefone").get(0);
		BigDecimal a = new BigDecimal("3589.00");
		Produto p1 = new Produto(1L,"iPhone Sete","Red",c, a ,"Apple");
		sp.insert(p1);
		Produto aux = (Produto) sp.findByName("iPhone Sete").get(0);
		assertNotNull(aux);
		log.info("============> Finalizando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	
	@Test
	public void teste03_inserirComErroProduto() throws Exception {
		
		log.info("============> Iniciando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());		
		try {
			BigDecimal a = new BigDecimal("3589.00");
			Categoria c = (Categoria) sc.findByName("Telefone").get(0);
			Produto p2 = new Produto(null,"iPhone 7","Red",c, a ,"Apple");
				sp.insert(p2);
		} 
		catch (Exception e){
				assertTrue(checkString(e, "Caracteres permitidos: letras, espaços, ponto e aspas simples"));
		}
		log.info("============> Finalizando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Test
	public void teste04_atualizarProduto() throws Exception {
		
		log.info("============> Iniciando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
		Produto o = (Produto) sp.findByName("iPhone Sete").get(0);
		o.setNome("iPhone Sete modificado");
		sp.update(o);
		Produto aux = (Produto) sp.findByName("iPhone Sete modificado").get(0);
		assertEquals("iPhone Sete modificado", aux.getNome());
		log.info("============> Finalizando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	
	@Test
	public void teste05_excluirCliente() throws Exception {
		
		log.info("============> Iniciando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
		Produto o = (Produto) sp.findByName("iPhone Sete").get(0);
		sp.delete(o);
		assertEquals(0, sp.findByName("iPhone Sete modificado").size());
		log.info("============> Finalizando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	
	@Test
	public void teste06_excluirCategoria_1() throws Exception {
		
		log.info("============> Iniciando o teste " +
		Thread.currentThread().getStackTrace()[1].getMethodName());
		Categoria o = (Categoria) sc.findByName("Telefone").get(0);
		sc.delete(o);
		assertEquals(0, sc.findByName("Telefone").size());
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
