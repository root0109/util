/**
 * 
 */
package com.zaprit.common.bo;

import java.io.Serializable;
import java.util.Currency;

/**
 * @author rupak.raushan
 *
 */
public class Money implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9093814795022919841L;

	private Currency	currency	= null;
	private double		price		= 0;

	/**
	 * 
	 */
	public Money()
	{}

	/**
	 * 
	 * @param currency
	 * @param price
	 */
	public Money(Currency currency, double price)
	{
		setCurrency(currency);
		setPrice(price);
	}

	/**
	 * 
	 * @param currency
	 * @param price
	 */
	public Money(String currency, double price)
	{
		setCurrency(Currency.getInstance(currency));
		setPrice(price);
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency()
	{
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency)
	{
		this.currency = currency;
	}

	/**
	 * @return the price
	 */
	public double getPrice()
	{
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price)
	{
		this.price = price;
	}

}
