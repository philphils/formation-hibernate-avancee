package fr.insee.formation.hibernate.batch.redressementMontantDeclaration;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import fr.insee.formation.hibernate.model.Declaration;

@Component
public class RedressementProcessor implements ItemProcessor<Declaration, Declaration> {

	@Override
	public Declaration process(Declaration declaration) throws Exception {

		/*
		 * Formule Redressement : ancienMontant * ( 1 +
		 * (coeffEntreprise*coeffSousClasse))
		 */
		declaration.setMontant(
				declaration.getMontant() * (1d + (declaration.getEntreprise().getCoeffRedressementEntreprise()
						* declaration.getEntreprise().getSousClasse().getCoeffRedressementNiveau())));

		return declaration;
	}

}
