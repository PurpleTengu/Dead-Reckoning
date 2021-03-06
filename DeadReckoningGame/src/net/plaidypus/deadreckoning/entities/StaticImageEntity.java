package net.plaidypus.deadreckoning.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

// TODO: Auto-generated Javadoc
/**
 * The Class StaticImageEntity.
 */
public abstract class StaticImageEntity extends Entity {

	/** The draw. */
	protected Image draw;

	/**
	 * Instantiates a new static image entity.
	 */
	public StaticImageEntity() {
	}

	/**
	 * Instantiates a new static image entity.
	 * 
	 * @param t
	 *            the t
	 * @param layer
	 *            the layer
	 * @param drawImage
	 *            the draw image
	 */
	public StaticImageEntity(Image drawImage) {
		super();
		draw = drawImage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.plaidypus.deadreckoning.entities.Entity#forceRender(org.newdawn.slick
	 * .Graphics, float, float)
	 */
	@Override
	public void forceRender(Graphics g, float x, float y) {
		g.drawImage(draw, x+16-draw.getWidth()/2, y+32-draw.getHeight());
	}

}
