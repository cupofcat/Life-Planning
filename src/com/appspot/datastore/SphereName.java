package com.appspot.datastore;

public enum SphereName {
	HEALTH{

		@Override
		public Double defaultValue() {
			return 0.2;
		}
	}, WORK {
		@Override
		public Double defaultValue() {
			return 0.4;
		}
	}, FAMILY {
		@Override
		public Double defaultValue() {
			return 0.25;
		}
	}, RECREATION {
		@Override
		public Double defaultValue() {
			return 0.15;
		}
	};
	
	public abstract Double defaultValue();
}
