package br.org.sigafrota.server;

/*
# Copyright 2010 - Prefeitura Municipal de Fortaleza
#
# Este arquivo é parte do Sistema Integrado de Gestão e Acompanhamento de Frotas
# SIGAFrota
# 
# O SIGAFrota é um software livre; você pode redistribui-lo e/ou modifica-lo
# dentro dos termos da Licença Pública Geral GNU como publicada pela
# Fundação do Software Livre (FSF); na versão 2 da Licença.
#
# Este programa é distribuido na esperança que possa ser util, mas SEM
# NENHUMA GARANTIA; sem uma garantia implicita de ADEQUAÇÂO a qualquer
# MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU
# para maiores detalhes.
#
# Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o
# título "LICENCA.txt", junto com este programa, se não, escreva para a
# Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,
*/

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {

	public static final Long SECOND_IN_MILLIS = 1000L;
	public static final Long MINUTE_IN_MILLIS = 60L * SECOND_IN_MILLIS;
	public static final Long HOUR_IN_MILLIS = 60L * MINUTE_IN_MILLIS;
	public static final Long DAY_IN_MILLIS = 24L * HOUR_IN_MILLIS;
	public static final String BR_DATE_PATTERN = "dd/MM/yyyy";
	public static final String DEFAULT_HOUR_PATTERN = "HH:mm:ss";
	public static final String US_DATE_PATTERN = "yyyy-MM-dd";
	public static final String US_DATE_PATTERN_GENERAL = "yyyy/MM/dd HH:mm:ss";
	public static final String DEFAULT_DATE_TIME_PATTERN = BR_DATE_PATTERN + " " + DEFAULT_HOUR_PATTERN;
	public static final String ARENA_DATE_TIME_PATTERN = US_DATE_PATTERN + " " + DEFAULT_HOUR_PATTERN;
	public static final String[] DAYS_OF_WEEK = { "Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado" };
	public static final String[] MONTHS_OF_YEAR = { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", 
		"Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" };

	public static final String[] MONTHS_OF_YEAR_SIMPLE = { "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez" };

	public static Date getDateNow() {
		return new Date(Calendar.getInstance().getTimeInMillis());
	}

	public static Boolean compareDate(Date first, Date second){

		if( first.getTime() <= second.getTime()){
			return true;
		} else {
			return false;
		}
	}

	public static String getDateNowAsString() {
		return getDateNowAsString(BR_DATE_PATTERN);
	}

	public static String getDateNowAsString(String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(getDateNow());
	}

	public static String getTimeNowAsString() {
		return getTimeNowAsString(DEFAULT_HOUR_PATTERN);
	}

	public static String getTimeNowAsString(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(getDateNow());
	}

	public static String parseDateAsString(Date value) {
		return parseAsString(BR_DATE_PATTERN, value);
	}

	public static Date parseUsGeneralDateTime(Date value) {
		SimpleDateFormat sdf = new SimpleDateFormat(US_DATE_PATTERN_GENERAL);
		return parseStringAsDate("yyyy/MM/dd HH:mm:ss", sdf.format(value));
	}

	public static String parseArenaDateAsString(Date value){
		return parseAsString(US_DATE_PATTERN, value);
	}

	public static String parseTimeAsString(Date value){
		return parseAsString(DEFAULT_HOUR_PATTERN, value);
	}

	public static String parseAsString(String pattern, Date value) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			return (value == null) ? null : formatter.format(value);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date parseArenaStringAsDateTime(String value) {
		return parseStringAsDate(ARENA_DATE_TIME_PATTERN, value);
	}

	public static Date parseStringAsDate(String value) {
		return parseStringAsDate(BR_DATE_PATTERN, value);
	}

	public static Date parseStringAsTime(String value) {
		return parseStringAsDate(DEFAULT_HOUR_PATTERN, value);
	}

	public static Date parseStringAsDate(String pattern, String value) {

		try {
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			return (value == null) ? null : formatter.parse(value);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date getNowDateTime(String time) {
		return getDateTime(getDateNow(), time);
	}

	public static Date getDateTime(Date date, String time) {

		Integer hour = Integer.valueOf(time.substring(0, 2));
		Integer minute = Integer.valueOf(time.substring(3, 5));
		Integer second = Integer.valueOf(time.substring(6));
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		return cal.getTime();
	}
	
	public static Date getDateTime(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getDateStartDay() {
		return getDateStartDay(getDateNow());
	}

	public static Date getDateStartDay(Date date) {
		return getDateTime(date, "00:00:00");
	}

	public static Date getDateEndDay() {
		return getDateEndDay(getDateNow());
	}

	public static Date getDateEndDay(Date date) {
		return getDateTime(date, "23:59:59");
	}

	public static String format(String oldPattern, String newPattern, String data) {
		Date date = parseStringAsDate(oldPattern, data);
		return parseAsString(newPattern, date);
	}

	public static Date adicionarOuDiminuir(Date data, Long tempo) {
		Long dataSubtraida = data.getTime() + tempo;
		return new Date(dataSubtraida);
	}

	public static Date tempoEntreDatas(Date dt1, Date dt2) {
		Date result = parseStringAsTime("00:00:00");
		Long l1 = dt1.getTime();
		Long l2 = dt2.getTime();
		return adicionarOuDiminuir(result, l1 > l2 ? (l1 - l2) : (l2 - l1));
	}

	public static Long tempoEntreDatasLong(Date dt1, Date dt2) {
		Long l1 = dt1.getTime();
		Long l2 = dt2.getTime();
		return l1 > l2 ? (l1 - l2) : (l2 - l1);
	}

	public static Long duracaoParada(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		long hour = cal.get(Calendar.HOUR_OF_DAY);
		long min = cal.get(Calendar.MINUTE);
		long sec = cal.get(Calendar.SECOND);
		return (hour * 3600L) + (min * 60L) + sec;
	}

	public static Date addTime(Date current, Date toadd) {

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(current);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(toadd);
		cal1.add(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
		cal1.add(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
		cal1.add(Calendar.SECOND, cal2.get(Calendar.SECOND));

		return cal1.getTime();
	}

	public static Date divideTime(Date data, int n){

		if(n > 0){
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());
			Calendar t = Calendar.getInstance();
			t.setTime(data);
			Calendar r = Calendar.getInstance();
			r.setTime(parseStringAsDate("yyyy/MM/dd HH:mm:ss", "1970/01/01 00:00:00"));
			int d = t.get(Calendar.DAY_OF_MONTH);
			int h = t.get(Calendar.HOUR_OF_DAY);
			int m = t.get(Calendar.MINUTE);
			int s = t.get(Calendar.SECOND);
			r.set(Calendar.DAY_OF_MONTH, ((int)((float) d/n == 0F ? 1 : d/n)));
			r.set(Calendar.HOUR, (24*((d-1)%n ) + h)/n);
			r.set(Calendar.MINUTE, (60*((24*((d-1)%n) + h)%n) + m)/n);
			r.set(Calendar.SECOND, ( 60*(60*((24*((d-1)%n) + h)%n) + m)%n) + s/n);
			return r.getTime();
		} else {
			return null;
		}
	}

	public static Date getDateAsTimeString(Date date){

		Calendar t = Calendar.getInstance();
		t.setTime(date);
		t.set(Calendar.DAY_OF_MONTH, 1);
		t.set(Calendar.MONTH, 1);
		t.set(Calendar.YEAR, 1970);
		return t.getTime();
	}

	/** 
	 * Retorna o valor do hor�rio minimo para a data de referencia passada. 
	 * <BR> 
	 * <BR> Por exemplo se a data for "30/01/2009 as 17h:33m:12s e 299ms" a data 
	 * retornada por este metodo ser� "30/01/2009 as 00h:00m:00s e 000ms". 
	 * @param date de referencia. 
	 * @return {@link Date} que representa o hor�rio minimo para dia informado. 
	 */  
	public static Date lowDateTime(Date date) {  
		Calendar aux = Calendar.getInstance();  
		aux.setTime(date);  
		toOnlyDate(aux); //zera os parametros de hour,min,sec,milisec  
		return aux.getTime();  
	}  

	/** 
	 * Retorna o valor do hor�rio maximo para a data de referencia passada. 
	 * <BR> 
	 * <BR> Por exemplo se a data for "30/01/2009 as 17h:33m:12s e 299ms" a data 
	 * retornada por este metodo ser� "30/01/2009 as 23h:59m:59s e 999ms". 
	 * @param date de referencia. 
	 * @return {@link Date} que representa o hor�rio maximo para dia informado. 
	 */  
	public static Date highDateTime(Date date) {  
		Calendar aux = Calendar.getInstance();  
		aux.setTime(date);  
		toOnlyDate(aux); //zera os parametros de hour,min,sec,milisec  
		aux.roll(Calendar.DATE, true); //vai para o dia seguinte  
		aux.roll(Calendar.MILLISECOND, false); //reduz 1 milisegundo  
		return aux.getTime();  
	}  

	/** 
	 * Zera todas as referencias de hora, minuto, segundo e milesegundo do 
	 * {@link Calendar}. 
	 * @param date a ser modificado. 
	 */  
	public static void toOnlyDate(Calendar date) {  
		date.set(Calendar.HOUR_OF_DAY, 0);  
		date.set(Calendar.MINUTE, 0);  
		date.set(Calendar.SECOND, 0);  
		date.set(Calendar.MILLISECOND, 0);  
	} 

}