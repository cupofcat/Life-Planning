package com.appspot.datastore;

public enum SphereName {
	HEALTH{

		@Override
		public int defaultValue() {
			return 30;
		}
	}, WORK {
		@Override
		public int defaultValue() {
			return 20;
		}
	}, FAMILY {
		@Override
		public int defaultValue() {
			return 40;
		}
	}, RECREATION {
		@Override
		public int defaultValue() {
			return 10;
		}
	};
	
	public abstract int defaultValue();
}
