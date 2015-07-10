package com.daneshzaki.thingse;

import android.graphics.drawable.Drawable;

//This is a POJO bean-like class to persist a thing's attributes for easy handling
public class Thing
{
	public Thing()
	{
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getId()
	{
		return id;
	}

	
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the price
	 */
	public String getPrice()
	{
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price)
	{
		this.price = price;
	}

	/**
	 * @return the purchaseDate
	 */
	public String getPurchaseDate()
	{
		return purchaseDate;
	}

	/**
	 * @param purchaseDate the purchaseDate to set
	 */
	public void setPurchaseDate(String purchaseDate)
	{
		this.purchaseDate = purchaseDate;
	}

		/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the picLocation
	 */
	public String getPicLocation()
	{
		return picLocation;
	}

	/**
	 * @param picLocation the picLocation to set
	 */
	public void setPicLocation(String picLocation)
	{
		this.picLocation = picLocation;
	}

	/**
	 * @return the pic
	 */
	public Drawable getPic()
	{
		return pic;
	}

	/**
	 * @param pic the pic to set
	 */
	public void setPic(Drawable pic)
	{
		this.pic = pic;
	}
	
	public String toString()
	{
		
		return id +" " + name + " "+ price + " "+purchaseDate+" "+description+" "+picLocation;
	}
	
	
	public String getDisplayString()
	{
		return name + " "+ price + " "+purchaseDate;
	}

	
	//variables
	private String id = "";
	private String name = "";
	private String price="";
	private String purchaseDate="";

	private String description = "";
	private String picLocation = "";
	private Drawable pic = null;
}
