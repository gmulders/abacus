package org.gertje.abacus.translator;

import org.gertje.abacus.runtime.F;
import org.junit.Test;

public class TranslationTest {

	@Test
	public void testTranslation() {

		(new F<Boolean>() {
			public Boolean f() {
				Boolean left = (new F<Boolean>() {
					public Boolean f() {
						Boolean left = Boolean.TRUE;

						if (!Boolean.FALSE.equals(left))
							return left;


						return Boolean.TRUE;
					}
				}).f();


				if (!Boolean.FALSE.equals(left))
					return left;


				return 		(new F<Boolean>() {
					public Boolean f() {
						Boolean left = Boolean.TRUE;

						if (!Boolean.FALSE.equals(left))
							return left;


						return Boolean.TRUE;
					}
				}).f();
			}
		}).f();

	}

}
