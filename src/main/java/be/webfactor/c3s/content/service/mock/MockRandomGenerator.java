package be.webfactor.c3s.content.service.mock;

import java.util.*;

import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.FieldContainer;

public final class MockRandomGenerator {

	private static final Random R = new Random();

	private static final List<String> ALINEAS = Arrays.asList(
			"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Vestibulum id ligula porta felis euismod semper. Sed posuere consectetur est at lobortis. Curabitur blandit tempus porttitor.",
			"Vivamus sagittis lacus vel augue laoreet rutrum faucibus dolor auctor. Donec sed odio dui. Vestibulum id ligula porta felis euismod semper. Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
			"Aenean lacinia bibendum nulla sed consectetur. Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Aenean lacinia bibendum nulla sed consectetur. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Nulla vitae elit libero, a pharetra augue. Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
			"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus sagittis lacus vel augue laoreet rutrum faucibus dolor auctor. Etiam porta sem malesuada magna mollis euismod. Integer posuere erat a ante venenatis dapibus posuere velit aliquet. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Praesent commodo cursus magna, vel scelerisque nisl consectetur et."
	);

	private static final List<String> SENTENCES = Arrays.asList(
			"Praesent commodo cursus magna, vel scelerisque nisl consectetur et.",
			"Sed posuere consectetur est at lobortis. Aenean eu leo quam.",
			"Pellentesque ornare sem lacinia quam venenatis vestibulum.",
			"Maecenas sed diam eget risus varius blandit sit amet non magna.",
			"Nullam quis risus eget urna mollis ornare vel eu leo."
	);

	private MockRandomGenerator() {}

	public static MockContentItem contentItem(String type) {
		return new MockContentItem(type);
	}

	public static List<ContentItem> contentItemList(String type, int count) {
		List<ContentItem> result = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			result.add(contentItem(type));
		}

		return result;
	}

	public static List<FieldContainer> groupItemList(String type, int count) {
		List<FieldContainer> result = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			result.add(groupItem(type));
		}

		return result;
	}

	public static int smallInt() {
		return R.nextInt(20);
	}

	public static int integer() {
		return R.nextInt(1000);
	}

	public static double number() {
		return R.nextDouble() * 100.0;
	}

	public static String alinea() {
		return randomElement(ALINEAS);
	}

	public static String sentence() {
		return randomElement(SENTENCES);
	}

	public static String url() {
		return "https://www.google.com";
	}

	private static FieldContainer groupItem(String type) {
		return new MockFieldContainer(type);
	}

	private static <E> E randomElement(List<E> elements) {
		return elements.get(R.nextInt(elements.size()));
	}
}
