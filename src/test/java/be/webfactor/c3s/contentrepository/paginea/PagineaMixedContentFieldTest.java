package be.webfactor.c3s.contentrepository.paginea;

import be.webfactor.c3s.contentrepository.domain.MixedContentItem;
import be.webfactor.c3s.contentrepository.paginea.model.PagineaMixedContentFieldDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that a "mixed-content" field, whose items are the polymorphic {@code Field} base,
 * deserializes into the concrete per-type DTOs via the {@code type} discriminator and is exposed
 * through the domain interface as keyed fields — templates branch on {@link MixedContentItem#getKey()}
 * and read the value through the matching typed getter, e.g. {@code item.getImage()}. Multiple items of
 * the same type each carry their own key.
 */
class PagineaMixedContentFieldTest {

	// Same mapper the generated resttemplate ApiClient relies on (RestTemplate's default converter).
	private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();

	private PagineaMixedContentField mixedContent;

	@BeforeEach
	void setUp() throws Exception {
		String json = """
			{
			  "type": "mixed-content",
			  "textValue": "body",
			  "items": [
			    { "type": "image", "key": "image", "textValue": "photo", "url": "https://cdn/x.jpg", "alt": "A cat" },
			    { "type": "image", "key": "thumbnail", "textValue": "thumb", "url": "https://cdn/thumb.jpg", "alt": "Small cat" },
			    { "type": "rich-text", "key": "intro", "textValue": "<p>Hello</p>" },
			    { "type": "text", "key": "caption", "textValue": "Just text" },
			    { "type": "number", "key": "price", "textValue": "42", "numberValue": 42.0 }
			  ]
			}
			""";

		PagineaMixedContentFieldDto dto = mapper.readValue(json, PagineaMixedContentFieldDto.class);
		mixedContent = new PagineaMixedContentField(dto);
	}

	@Test
	void exposesItemsInOrderWithTheirKeyAndType() {
		List<MixedContentItem> items = mixedContent.getItems();

		assertEquals(
				List.of("image", "thumbnail", "intro", "caption", "price"),
				items.stream().map(MixedContentItem::getKey).toList());
		assertEquals(
				List.of("image", "image", "rich-text", "text", "number"),
				items.stream().map(MixedContentItem::getType).toList());
	}

	@Test
	void sameTypeItemsEachExposeTheirOwnValue() {
		List<MixedContentItem> items = mixedContent.getItems();

		MixedContentItem image = items.get(0);
		MixedContentItem thumbnail = items.get(1);

		assertEquals("https://cdn/x.jpg", image.getImage().getUrl());
		assertEquals("https://cdn/thumb.jpg", thumbnail.getImage().getUrl());
	}

	@Test
	void readsEachValueThroughItsTypedGetter() {
		List<MixedContentItem> items = mixedContent.getItems();

		assertEquals("<p>Hello</p>", items.get(2).getRichText().getHtml());
		assertEquals("Just text", items.get(3).getText());
		assertNotNull(items.get(4).getNumber());
	}

	@Test
	void nonMatchingTypeReturnsNull() {
		MixedContentItem image = mixedContent.getItems().get(0);

		assertNull(image.getRichText());   // this item is an image, not rich text
		assertNull(image.getNumber());
	}
}
