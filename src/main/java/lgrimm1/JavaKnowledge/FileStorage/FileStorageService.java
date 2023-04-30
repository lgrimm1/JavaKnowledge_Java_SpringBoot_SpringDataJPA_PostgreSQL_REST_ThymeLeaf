package lgrimm1.JavaKnowledge.FileStorage;

import lgrimm1.JavaKnowledge.Common.*;
import lgrimm1.JavaKnowledge.Process.*;
import lgrimm1.JavaKnowledge.Title.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.servlet.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * @see #FileStorageService(FileStorageRepository, TitleRepository, Formulas, ProcessRecords)
 * @see #uploadFiles(Payload, List)
 * @see #findAll()
 * @see #deleteAllFiles()
 * @see #handleMaxSizeException(String)
 */
@Service
public class FileStorageService {

	private final FileStorageRepository repository;
	private final TitleRepository titleRepository;
	private final Formulas formulas;
	private final ProcessRecords processRecords;

	@Autowired
	public FileStorageService(FileStorageRepository repository, TitleRepository titleRepository, Formulas formulas, ProcessRecords processRecords) {
		this.repository = repository;
		this.titleRepository = titleRepository;
		this.formulas = formulas;
		this.processRecords = processRecords;
		if (!repository.init("." + File.separator + "upload", true)) {
			throw new RuntimeException("Could not initialize the file storage!");
		}
	}

	/**
	 * Returns long[] where [0]: original amount of files, [1]: amount of successfully uploaded files.
	 */
	public long[] uploadFiles(Payload payload, List<Multipart> files) {
		if (payload == null || payload.getConfirm() == null || !payload.getConfirm() || files == null || files.size() == 0) {
			return new long[]{0, 0};
		}
		files = files.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		return new long[]{files.size(), repository.saveAll(files).count()};
	}

	public Stream<Path> findAll() {
		return repository.findAll();
	}

	/**
	 * Returns long[] where [0]: original amount of files, [1]: amount of successfully deleted files.
	 */
	public long[] deleteAllFiles() {
		long count = repository.count();
		long deleted = repository.deleteAll();
		return new long[]{count, deleted};
	}

	public ModelAndView handleMaxSizeException(String initialView) {
		Payload payload = new Payload(
				formulas.getTitleManagement(),
				false,
				null,
				null,
				"ONE OR MORE FILE(S) EXCEEDED 1MB!",
				null,
				null,
				"",
				processRecords.getAllTitles(titleRepository)
		);
		return new ModelAndView(initialView, "payload", payload);
	}
}