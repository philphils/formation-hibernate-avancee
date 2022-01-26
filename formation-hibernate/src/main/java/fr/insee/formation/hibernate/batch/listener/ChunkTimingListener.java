package fr.insee.formation.hibernate.batch.listener;

import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

import fr.insee.formation.hibernate.batch.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChunkTimingListener implements ChunkListener {

	private LocalTime dateDebut;
	private Integer chunkSize;

	public ChunkTimingListener(Integer chunkSize) {
		this.chunkSize = chunkSize;
	}

	@Override
	public void beforeChunk(ChunkContext context) {
		dateDebut = LocalTime.now(ZoneId.of("Europe/Paris"));
	}

	@Override
	public void afterChunk(ChunkContext context) {
		LocalTime dateFin = LocalTime.now(ZoneId.of("Europe/Paris"));
		log.debug("Le chunk de taille {} s'est exécuté en {}.", chunkSize,
				TimeUtils.differenceBetween(dateDebut, dateFin));
	}

	@Override
	public void afterChunkError(ChunkContext context) {
		// TODO Auto-generated method stub

	}

}
