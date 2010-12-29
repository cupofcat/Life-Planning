package com.appspot.datastore;

public enum SphereName {
	HEALTH {
		@Override
		public Double defaultValue() {
			return 0.25;
		}
	}, 
	WORK {
		@Override
		public Double defaultValue() {
			return 0.25;
		}
	}, 
	FAMILY {
		@Override
		public Double defaultValue() {
			return 0.25;
		}
	}, 
	RECREATION {
		@Override
		public Double defaultValue() {
			return 0.25;
		}
	};

	public abstract Double defaultValue();

	public static SphereName getSphereName(String name) {
		if (name.toUpperCase().equals("HEALTH"))
			return SphereName.HEALTH;
		if (name.toUpperCase().equals("WORK"))
			return SphereName.WORK;
		if (name.toUpperCase().equals("FAMILY"))
			return SphereName.FAMILY;
		if (name.toUpperCase().equals("RECREATION"))
			return SphereName.RECREATION;
		return null;
	}
}
