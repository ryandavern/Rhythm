package com.beatplaylist.enums;

public enum GenreType {

	NONE("None"), //
	ALL_TYPES("All Types"), //
	ALTERNATIVE_MUSIC("Alternative Music"), //
	BLUES("Blues"), //
	CLASSICAL_MUSIC("Classical Music"), //
	COUNTRY_MUSIC("Country Music"), //
	DANCE_MUSIC("Dance Music"), //
	EASY_LISTENING("Easy Listening"), //
	ELECTRONIC_MUSIC("Electronic Music"), //
	EUROPEAN_MUSIC("European Music"), //
	HIP_HOP("Hip Hop"), //
	RAP("Rap"), //
	INDIE_POP("Indie Pop"), //
	INSPIRATIONAL("Inspirational"), //
	ASIAN_POP("Asian Pop"), //
	JAZZ("Jazz"), //
	LATIN_MUSIC("Latin Music"), //
	NEW_AGE("New Age"), //
	OPERA("Opera"), //
	POP("Pop"), //
	R_AND_B("R&B"), //
	REGGAE("Reggae"), //
	ROCK("Rock"), //
	SINGER("Singer"), //
	BEATS("Beats");

	private String name;

	GenreType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public static GenreType getName(String name) {
		GenreType genre = GenreType.valueOf(name.toUpperCase());
		return genre;
	}

	public static GenreType getGenreFromValue(String name) {
		for (GenreType genres : GenreType.values()) {
			if (genres.getName().equals(name))
				return genres;
		}
		return GenreType.ALL_TYPES;
	}
}