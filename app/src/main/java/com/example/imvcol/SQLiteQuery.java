package com.example.imvcol;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SQLiteQuery {

    public String query;
    public final static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SQLiteQuery() {
    }

    public SQLiteQuery(String query) {
        this.query = query;
    }

    //Retorna datos de la base de datos dada una consulta sql(query).
    public Object[][] getRecords(SQLiteDatabase db) throws Exception {
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            Object data[][] = new Object[c.getCount()][c.getColumnCount()];
            do {
                for (int i = 0; i < c.getColumnCount(); i++) {
                    if (c.getType(i) == Cursor.FIELD_TYPE_FLOAT) {
                        data[c.getPosition()][i] = c.getFloat(i);
                    } else if (c.getType(i) == Cursor.FIELD_TYPE_INTEGER) {
                        data[c.getPosition()][i] = c.getLong(i);
                    } else if (c.getType(i) == Cursor.FIELD_TYPE_STRING) {
                        Pattern pat = Pattern.compile("[\\\\d]{4}-[\\\\d]{2}-[\\\\d]{2} [\\\\d]{2}:[\\\\d]{2}:[\\\\d]{2}");
                        if (pat.matcher(c.getString(i)).matches()) { //si es fecha
                            Date date = dateTimeFormat.parse(c.getString(i));
                            data[c.getPosition()][i] = date;
                        } else {
                            data[c.getPosition()][i] = c.getString(i);
                        }
                    } else if (c.getType(i) == Cursor.FIELD_TYPE_BLOB) {
                        data[c.getPosition()][i] = c.getBlob(i);
                    } else if (c.getType(i) == Cursor.FIELD_TYPE_NULL) {
                        data[c.getPosition()][i] = null;
                    }
                }
            }
            while (c.moveToNext());
            return data;
        }
        return null;
    }

    public Object[] getRecord(SQLiteDatabase db) throws Exception {
        Object[][] records = getRecords(db);
        if (records != null) {
            if (records.length > 1) {
                throw new Exception("Operación inválida");
            } else if (records[0] != null && records.length == 1) {
                return records[0];
            }
        }
        return null;
    }

    public Object getObject(SQLiteDatabase db) throws Exception {
        Object[] record = getRecord(db);
        if (record != null) {
            if (record.length > 1) {
                throw new Exception("Operación inválida");
            } else if (record[0] != null && record.length == 1) {
                return record[0];
            }
        }
        return null;
    }

    public Integer getInteger(SQLiteDatabase db) throws Exception {
        Object obj = getObject(db);
        if (obj != null) {
            return getAsInteger(obj);
        }
        return null;
    }

    public Long getLong(SQLiteDatabase db) throws Exception {
        Object obj = getObject(db);
        if (obj != null) {
            return getAsLong(obj);
        }
        return null;
    }

    public Integer getAsInteger(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else {
            throw new RuntimeException("No se puede convertir " + obj.getClass().getSimpleName() + " a long");
        }

    }

    public Long getAsLong(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        } else if (obj instanceof Long) {
            return ((Long) obj);
        } else {
            throw new RuntimeException("No se puede convertir " + obj.getClass().getSimpleName() + " a long");
        }

    }

    public static String getAsString(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof byte[]) {
            return new String((byte[]) o);
        }
        return o.toString();
    }

    public SQLiteQuery setParam(int par, Object val) {
        this.query = setParam(query, par, val);
        return this;
    }

    public static String setParam(String query, int par, Object val) {
        String v = null;
        if (val != null) {
            if (val instanceof Integer) {
                v = val.toString();
            } else if (val instanceof String) {
                if (((String) val).trim().isEmpty()) {
                    v = "NULL";
                } else {
                    v = "\"" + scape((String) val) + "\"";
                }
                //para las comillas escapadas en el replace all
                v = Matcher.quoteReplacement(v);
            } else if (val instanceof Boolean) {
                if (((Boolean) val)) {
                    v = "1";
                } else {
                    v = "0";
                }
            } else if (val instanceof Date) {
                v = "\"" + SQLiteQuery.dateTimeFormat.format((Date) val) + "\"";
            } else if (val instanceof BigDecimal) {
                v = ((BigDecimal) val).toPlainString();
            } else if (val instanceof Double) {
                v = ((Double) val).toString();
            } else if (val instanceof byte[]) {
                byte[] hChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
                byte[] b = (byte[]) val;
                StringBuilder s = new StringBuilder("0x");
                for (int i = 0; i < b.length; i++) {
                    int v1 = b[i] & 0xff;
                    s.append((char) hChars[v1 >> 4]);
                    s.append((char) hChars[v1 & 0xf]);
                }
                v = s.toString();
            } else if (val instanceof BigInteger) {
                v = val.toString();
            } else {
                throw new RuntimeException("tipo no soportado " + val.getClass().toString());
            }
        } else {
            v = "NULL";
        }
        query = query.replaceAll("\\?" + par + ",", v + ",");
        query = query.replaceAll("\\?" + par + "[\\s]", v + " ");
        query = query.replaceAll("\\?" + par + "[)]", v + ")");
        query = query.replaceAll("\\?" + par + "\\z", v + " ");
        return query;
    }

    //PARA EVITAR LA INYECCIÓN SQL SE ESCAPAN CARACTERES
    public static String scape(String str) {
        String rta = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\'':
                case '\"':
                    rta += "\\" + c;
                    break;
                case '\\':
                    if (i < str.length() - 1) {
                        char c1 = str.charAt(i + 1);
                        switch (c1) {
                            case '\'':
                            case '\"':
                            case '\\':
                                rta += c;
                                rta += c1;
                                i++;
                                break;
                            default:
                                rta += "\\\\";
                                break;
                        }
                    } else {
                        rta += "\\\\";
                    }
                    break;
                default:
                    rta += c;
                    break;
            }
        }
        return rta;
    }


}

