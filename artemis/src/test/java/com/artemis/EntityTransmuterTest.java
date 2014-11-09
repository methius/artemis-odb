package com.artemis;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.Packed;
import com.artemis.component.ReusedComponent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EntityTransmuterTest {

	private World world;
	private EntityTransmuter transmuter3;

	@Before
	public void init() {
		world = new World();
		world.initialize();

		transmuter3 = new EntityTransmuterFactory(world)
			.add(ComponentX.class)
			.add(Packed.class)
			.remove(ComponentY.class)
			.build();
	}
	
	@Test
	public void transmuting_entities_test() {
		Entity e1 = createEntity(ComponentY.class, ReusedComponent.class);
		Entity e2 = createEntity(ComponentY.class, ReusedComponent.class);

		world.process();
		assertEquals(2, e1.getCompositionId());

		transmuter3.transmute(e1);

		// manually applying transmuter to e2
		EntityEdit edit = e2.edit();
		edit.create(ComponentX.class);
		edit.create(Packed.class);
		edit.remove(ComponentY.class);

		world.process();

		assertTrue("compositionId=" + e2.getCompositionId(), 2 != e2.getCompositionId());
		assertEquals(e1.getCompositionId(), e2.getCompositionId());
	}

	private Entity createEntity(Class<? extends Component>... components) {
		Entity e = world.createEntity();
		EntityEdit edit = e.edit();
		for (Class<? extends Component> c : components)
			edit.create(c);

		return e;
	}
}