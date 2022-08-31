package fr.insee.formation.hibernate.batch;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.formation.hibernate.batch.calculIndices.CalculIndicesProcessor;
import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import net.ttddyy.dsproxy.QueryCountHolder;

public class CalculIndicesProcessorTest extends AbstractTest {

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	@Autowired
	private CalculIndicesProcessor calculIndicesProcessor;

	private static boolean databaseIsInitialized = false;

	@Before
	public void testMappingAssociation() {
		if (!databaseIsInitialized) {
			jeuxTestUtil.creerJeuNouvelleNomenclatureAvecIndice();
			databaseIsInitialized = true;
		}

		ouvrirNouvelleTransaction();

		QueryCountHolder.clear();

	}

	@Test
	public void testCalculIndicesProcessor() {

	}

}
