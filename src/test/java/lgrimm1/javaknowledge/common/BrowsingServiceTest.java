package lgrimm1.javaknowledge.common;

import lgrimm1.javaknowledge.html.*;
import lgrimm1.javaknowledge.process.*;
import lgrimm1.javaknowledge.title.*;
import lgrimm1.javaknowledge.txt.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.mockito.Mockito.*;

class BrowsingServiceTest {

	TitleRepository titleRepository;
	TxtRepository txtRepository;
	HtmlRepository htmlRepository;
	Formulas formulas;
	ProcessRecords processRecords;
	BrowsingService browsingService;

	@BeforeEach
	void setUp() {
		titleRepository = Mockito.mock(TitleRepository.class);
		txtRepository = Mockito.mock(TxtRepository.class);
		htmlRepository = Mockito.mock(HtmlRepository.class);
		formulas = Mockito.mock(Formulas.class);
		processRecords = Mockito.mock(ProcessRecords.class);
		browsingService = new BrowsingService(
				titleRepository,
				txtRepository,
				htmlRepository,
				processRecords,
				formulas);
		when(formulas.getTitleRoot())
				.thenReturn("ROOTTITLE");
		when(formulas.getTitleManagement())
				.thenReturn("MANAGEMENTTITLE");
		when(formulas.getTitleSource())
				.thenReturn("SOURCETITLE");
	}

	@Test
	void getRoot() {
		Payload expectedPayload = new Payload(
				formulas.getTitleRoot(),
				null,
				null,
				null,
				null,
				"",
				null,
				null,
				null
		);

		Assertions.assertEquals(expectedPayload, browsingService.getRoot());
/*
		Map<String, Object> model = new HashMap<>();
		model.put("payload", expectedPayload);

		ModelAndView modelAndView = browsingService.getRoot("root");
		ModelAndViewAssert.assertViewName(modelAndView, "root");
		ModelAndViewAssert.assertModelAttributeValues(modelAndView, model);
*/
	}

	@Test
	void searchPages_WrongPayload() {
		Exception e = Assertions.assertThrows(Exception.class, () -> browsingService.searchPages(null));
		Assertions.assertEquals("THERE WAS A COMMUNICATION ERROR BETWEEN THE BROWSER AND THE SERVER.",
				e.getMessage());

		Payload receivedPayload = new Payload(
				"templateTitle",
				null,
				null,
				null,
				"",
				null,
				null,
				null,
				List.of("Title 1")
		);
		e = Assertions.assertThrows(Exception.class, () -> browsingService.searchPages(receivedPayload));
		Assertions.assertEquals("THERE WAS A COMMUNICATION ERROR BETWEEN THE BROWSER AND THE SERVER.",
				e.getMessage());
	}

	@Test
	void searchPages_RightPayload() {
		String searchText = "Word2 Word1";
		Payload receivedPayload = new Payload(
				formulas.getTitleRoot(),
				null,
				null,
				null,
				null,
				searchText,
				null,
				null,
				null
		);

		Set<String> titlesSet = Set.of("Title 2", "Title 1");
		when(processRecords.searchBySearchText(searchText, titleRepository, txtRepository))
				.thenReturn(titlesSet);
		List<String> titles = List.of("Title 1", "Title 2");
		Payload expectedPayload = new Payload(
				formulas.getTitleList(),
				null,
				null,
				null,
				null,
				searchText,
				null,
				null,
				titles
		);
	Assertions.assertEquals(expectedPayload, browsingService.searchPages(receivedPayload));
	}

	@Test
	void getPage_NullPayload() {
		Exception e = Assertions.assertThrows(Exception.class, () -> browsingService.getPage(null));
		Assertions.assertEquals("THERE WAS A COMMUNICATION ERROR BETWEEN THE BROWSER AND THE SERVER.",
				e.getMessage());
	}

	@Test
	void getPage_NullTitles() {
		Payload receivedPayload = new Payload(
				"templateTitle",
				null,
				null,
				null,
				null,
				"search text",
				null,
				null,
				null
		);
		Exception e = Assertions.assertThrows(Exception.class, () -> browsingService.getPage(receivedPayload));
		Assertions.assertEquals("THERE WAS A COMMUNICATION ERROR BETWEEN THE BROWSER AND THE SERVER.",
				e.getMessage());
	}

	@Test
	void getPage_EmptyTitles() {
		Payload receivedPayload = new Payload(
				"templateTitle",
				null,
				null,
				null,
				null,
				"search text",
				null,
				null,
				new ArrayList<>()
		);
		Exception e = Assertions.assertThrows(Exception.class, () -> browsingService.getPage(receivedPayload));
		Assertions.assertEquals("THERE WAS A COMMUNICATION ERROR BETWEEN THE BROWSER AND THE SERVER.",
				e.getMessage());
	}

	@Test
	void getPage_WrongPayload_MoreThanOneTitles() {
		Payload receivedPayload = new Payload(
				"templateTitle",
				null,
				null,
				null,
				null,
				"search text",
				null,
				null,
				List.of("Title 1", "Title 2")
		);
		Exception e = Assertions.assertThrows(Exception.class, () -> browsingService.getPage(receivedPayload));
		Assertions.assertEquals("THERE WAS A COMMUNICATION ERROR BETWEEN THE BROWSER AND THE SERVER.",
				e.getMessage());
	}

	@Test
	void getPage_WrongPayload_FirstTitlesIsNull() {
		List<String> titles = new ArrayList<>();
		titles.add(null);
		Payload receivedPayload = new Payload(
				"templateTitle",
				null,
				null,
				null,
				null,
				"search text",
				null,
				null,
				titles
		);
		Exception e = Assertions.assertThrows(Exception.class, () -> browsingService.getPage(receivedPayload));
		Assertions.assertEquals("THERE WAS A COMMUNICATION ERROR BETWEEN THE BROWSER AND THE SERVER.",
				e.getMessage());
	}

	@Test
	void getPage_WrongPayload_FirstTitlesIsBlank() {
		Payload receivedPayload = new Payload(
				"templateTitle",
				null,
				null,
				null,
				null,
				"search text",
				null,
				null,
				List.of("  ")
		);
		Exception e = Assertions.assertThrows(Exception.class, () -> browsingService.getPage(receivedPayload));
		Assertions.assertEquals("THERE WAS A COMMUNICATION ERROR BETWEEN THE BROWSER AND THE SERVER.",
				e.getMessage());
	}

	@Test
	void getPage_WrongPayload_NotExistingTitle() {
		String title = "Title 3";
		Payload receivedPayload = new Payload(
				"templateTitle",
				null,
				null,
				null,
				null,
				"search text",
				null,
				null,
				List.of(title)
		);
		when(titleRepository.findByTitle(title))
				.thenReturn(Optional.empty());
		Exception e = Assertions.assertThrows(Exception.class, () -> browsingService.getPage(receivedPayload));
		Assertions.assertEquals("THERE WAS A COMMUNICATION ERROR BETWEEN THE BROWSER AND THE SERVER.",
				e.getMessage());
	}

	@Test
	void getPage_WrongPayload_NotExistingHtml() {
		String title = "Title 3";
		Payload receivedPayload = new Payload(
				"templateTitle",
				null,
				null,
				null,
				null,
				"search text",
				null,
				null,
				List.of(title)
		);
		when(titleRepository.findByTitle(title))
				.thenReturn(Optional.of(new TitleEntity(1L, "Title 3", "title_3", 1L, 1L)));
		when(htmlRepository.findById(1L))
				.thenReturn(Optional.empty());
		Exception e = Assertions.assertThrows(Exception.class, () -> browsingService.getPage(receivedPayload));
		Assertions.assertEquals("THERE WAS A COMMUNICATION ERROR BETWEEN THE BROWSER AND THE SERVER.",
				e.getMessage());
	}

	@Test
	void getPage_RightPayload() {
		String title = "Title 3";
		List<String> contentList = List.of("content");
		String contentString = "content\n";
		List<String> titleReferences = List.of("Title 2");
		Payload receivedPayload = new Payload(
				"templateTitle",
				null,
				null,
				null,
				null,
				"search text",
				null,
				null,
				List.of(title)
		);
		when(titleRepository.findByTitle(title))
				.thenReturn(Optional.of(new TitleEntity(1L, "Title 3", "title_3", 1L, 1L)));
		when(htmlRepository.findById(1L))
				.thenReturn(Optional.of(new HtmlEntity(1L, contentList, titleReferences)));
		when(processRecords.listToString(contentList))
				.thenReturn(contentString);
		Payload expectedPayload = new Payload(
				formulas.getTitlePage(),
				null,
				null,
				null,
				null,
				null,
				contentString,
				null,
				titleReferences
		);
		Assertions.assertEquals(expectedPayload, browsingService.getPage(receivedPayload));
	}
}