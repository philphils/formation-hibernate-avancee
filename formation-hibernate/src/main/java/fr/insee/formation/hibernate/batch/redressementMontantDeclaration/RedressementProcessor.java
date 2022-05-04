package fr.insee.formation.hibernate.batch.redressementMontantDeclaration;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import fr.insee.formation.hibernate.model.Declaration;

@Component
public class RedressementProcessor implements ItemProcessor<Declaration, Declaration> {

	@Override
	public Declaration process(Declaration declaration) throws Exception {

		//// @formatter:off
		declaration.setMontant(
				declaration.getMontant() 
				* declaration.getEntreprise().getCoeffRedressementEntreprise()
				* declaration.getEntreprise().getSecteur().getCoeffRedressementNiveau()
				);
		// @formatter:on

		return declaration;
	}

}
