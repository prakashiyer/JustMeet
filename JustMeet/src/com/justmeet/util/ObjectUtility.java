package com.justmeet.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ObjectUtility {

	private static final Class[] EMPTYPARAMS = new Class[0];

	public static String convert(Object obj) {
		Class clazz = obj.getClass();
		String clazzName = clazz.getName();
		String className = getFormattedName(clazz.getName());

		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(className);
			doc.appendChild(rootElement);

			if (clazzName.equals("java.util.List")
					|| clazzName.equals("java.util.ArrayList")
					|| clazzName.equals("java.util.LinkedList")) {
				convertList(obj, doc, rootElement);
			} else if (clazzName.contains("java.util.Queue")
					|| clazzName.contains("java.util.PriorityQueue")
					|| clazzName.contains("java.util.LinkedList")) {
				convertQueue(obj, doc, rootElement);
			} else if (clazzName.contains("java.util.Deque")
					|| clazzName.contains("java.util.LinkedList")) {
				convertDeque(obj, doc, rootElement);
			} else if (clazzName.contains("java.util.Set")
					|| clazzName.contains("java.util.HashSet")
					|| clazzName.contains("java.util.TreeSet")
					|| clazzName.contains("java.util.LinkedHashSet")) {
				convertSet(obj, doc, rootElement);
			} else if (clazzName.contains("Map")
					|| clazzName.contains("ConcurrentMap")
					|| clazzName.contains("HashMap")
					|| clazzName.contains("TreeMap")
					|| clazzName.contains("LinkedHashMap")
					|| clazzName.contains("ConcurrentHashMap")) {
				convertMap(obj, doc, rootElement);
			} else {
				convert(obj, doc, rootElement, clazz);
			}
			

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			/*
			 * StreamResult result = new StreamResult(System.out);
			 * transformer.transform(source, result);
			 */

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// initialize StreamResult with File object to save to file
			StreamResult stringResult = new StreamResult(new StringWriter());

			transformer.transform(source, stringResult);
			String xmlString = stringResult.getWriter().toString();
			
			return format(xmlString).replace(
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
					"");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";

	}
	
	private static String format(String unformattedXml) {
        try {
            final Document document = parseXmlFile(unformattedXml);

            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(3);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	public static void convertList(Object parent, Document doc,
			Element rootElement){
		List fieldObj = (List) parent;
		if(fieldObj != null){
			for (Object listEntry : fieldObj) {
				Class listEntryClazz = listEntry.getClass();
				String listEntryClazzName = listEntryClazz.getName();
				if (isJavaClass(listEntryClazzName)) {
					Element listEntryElement = doc
							.createElement(getFormattedName(listEntryClazzName));
					rootElement.appendChild(listEntryElement);
					listEntryElement.appendChild(doc
							.createTextNode(String.valueOf(listEntry)));
				} else {
					Element listEntryElement = doc
							.createElement(getFormattedName(listEntryClazzName));
					rootElement.appendChild(listEntryElement);
					convert(listEntry, doc, listEntryElement,
							listEntryClazz);
				}
			}
		}					

	}
	
	public static void convertQueue(Object parent, Document doc,
			Element rootElement){
		Queue fieldObj = (Queue) parent;
		if(fieldObj != null){
			for (Object queueEntry : fieldObj) {
				Class queueEntryClazz = queueEntry.getClass();
				String queueEntryClazzName = queueEntryClazz.getName();
				if (isJavaClass(queueEntryClazzName)) {
					Element queueEntryElement = doc
							.createElement(getFormattedName(queueEntryClazzName));
					rootElement.appendChild(queueEntryElement);
					queueEntryElement.appendChild(doc
							.createTextNode(String.valueOf(queueEntry)));
				} else {
					Element queueEntryElement = doc
							.createElement(getFormattedName(queueEntryClazzName));
					rootElement.appendChild(queueEntryElement);
					convert(queueEntry, doc, queueEntryElement,
							queueEntryClazz);
				}
			}
		}					

	}
	
	public static void convertDeque(Object parent, Document doc,
			Element rootElement){
		Deque fieldObj = (Deque) parent;
		if(fieldObj != null){
			for (Object queueEntry : fieldObj) {
				Class queueEntryClazz = queueEntry.getClass();
				String queueEntryClazzName = queueEntryClazz.getName();
				if (isJavaClass(queueEntryClazzName)) {
					Element queueEntryElement = doc
							.createElement(getFormattedName(queueEntryClazzName));
					rootElement.appendChild(queueEntryElement);
					queueEntryElement.appendChild(doc
							.createTextNode(String.valueOf(queueEntry)));
				} else {
					Element queueEntryElement = doc
							.createElement(getFormattedName(queueEntryClazzName));
					rootElement.appendChild(queueEntryElement);
					convert(queueEntry, doc, queueEntryElement,
							queueEntryClazz);
				}
			}
		}					

	}
	
	public static void convertSet(Object parent, Document doc,
			Element rootElement){
		Set fieldObj = (Set) parent;
		if(fieldObj != null){
			for (Object setEntry : fieldObj) {
				Class setEntryClazz = setEntry.getClass();
				String setEntryClazzName = setEntryClazz.getName();
				if (isJavaClass(setEntryClazzName)) {
					Element setEntryElement = doc
							.createElement(getFormattedName(setEntryClazzName));
					rootElement.appendChild(setEntryElement);
					setEntryElement.appendChild(doc
							.createTextNode(String.valueOf(setEntry)));
				} else {
					Element setEntryElement = doc
							.createElement(getFormattedName(setEntryClazzName));
					rootElement.appendChild(setEntryElement);
					convert(setEntry, doc, setEntryElement,
							setEntryClazz);
				}
			}
		}					

	}
	
	public static void convertMap(Object parent, Document doc,
			Element rootElement){
		Map fieldObj = (Map) parent;
		Set<Entry<Object, Object>> entrySet = fieldObj.entrySet();
		if(fieldObj != null){
			for (Entry<Object, Object> entry : entrySet) {
				Object key = entry.getKey();
				Class keyClazz = key.getClass();
				String keyClazzName = keyClazz.getName();

				Object value = entry.getValue();
				Class valueClazz = value.getClass();
				String valueClazzName = valueClazz.getName();

				if (isJavaClass(keyClazzName)) {
					Element keyElement = doc.createElement(String
							.valueOf(key));
					rootElement.appendChild(keyElement);

					if (isJavaClass(valueClazzName)) {
						keyElement.appendChild(doc
								.createTextNode(String.valueOf(value)));
					} else {
						Element valueElement = doc
								.createElement(getFormattedName(valueClazzName));
						keyElement.appendChild(valueElement);
						convert(value, doc, valueElement, valueClazz);
					}
				}

			}
		}					

	}

	public static void convert(Object parent, Document doc,
			Element rootElement, Class clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			String fieldType = field.getType().getName();
			String fieldName = field.getName();
			try {
				if (isPrimitiveElement(fieldType)) {
					Method method = clazz.getMethod(getMethodName(fieldName),
							EMPTYPARAMS);
					Object fieldObj = method.invoke(parent, new Object[0]);
					Element element = doc.createElement(fieldName);

					rootElement.appendChild(element);
					element.appendChild(doc.createTextNode(String
							.valueOf(fieldObj)));
				} else if (fieldType.equals("java.util.List")
						|| fieldType.equals("java.util.ArrayList")
						|| fieldType.equals("java.util.LinkedList")) {
					Method method = clazz.getMethod(getMethodName(fieldName),
							EMPTYPARAMS);
					List fieldObj = (List) method.invoke(parent, new Object[0]);
					Element element = doc.createElement(fieldName);
					rootElement.appendChild(element);
					if(fieldObj != null){
						for (Object listEntry : fieldObj) {
							Class listEntryClazz = listEntry.getClass();
							String listEntryClazzName = listEntryClazz.getName();
							if (isJavaClass(listEntryClazzName)) {
								Element listEntryElement = doc
										.createElement(getFormattedName(listEntryClazzName));
								element.appendChild(listEntryElement);
								listEntryElement.appendChild(doc
										.createTextNode(String.valueOf(listEntry)));
							} else {
								Element listEntryElement = doc
										.createElement(getFormattedName(listEntryClazzName));
								element.appendChild(listEntryElement);
								convert(listEntry, doc, listEntryElement,
										listEntryClazz);
							}
						}
					}					
				} else if (fieldType.contains("java.util.Queue")
						|| fieldType.contains("java.util.PriorityQueue")
						|| fieldType.contains("java.util.LinkedList")) {
					Method method = clazz.getMethod(getMethodName(fieldName),
							EMPTYPARAMS);
					Queue fieldObj = (Queue) method.invoke(parent, new Object[0]);
					Element element = doc.createElement(fieldName);
					rootElement.appendChild(element);
					if(fieldObj != null){
						for (Object queueEntry : fieldObj) {
							Class queueEntryClazz = queueEntry.getClass();
							String queueEntryClazzName = queueEntryClazz.getName();
							if (isJavaClass(queueEntryClazzName)) {
								Element queueEntryElement = doc
										.createElement(getFormattedName(queueEntryClazzName));
								element.appendChild(queueEntryElement);
								queueEntryElement.appendChild(doc
										.createTextNode(String.valueOf(queueEntry)));
							} else {
								Element queueEntryElement = doc
										.createElement(getFormattedName(queueEntryClazzName));
								element.appendChild(queueEntryElement);
								convert(queueEntry, doc, queueEntryElement,
										queueEntryClazz);
							}
						}
					}
					
				} else if (fieldType.contains("java.util.Deque")
						|| fieldType.contains("java.util.LinkedList")) {
					Method method = clazz.getMethod(getMethodName(fieldName),
							EMPTYPARAMS);
					Deque fieldObj = (Deque) method.invoke(parent, new Object[0]);
					Element element = doc.createElement(fieldName);
					rootElement.appendChild(element);
					if(fieldObj != null){
						for (Object queueEntry : fieldObj) {
							Class queueEntryClazz = queueEntry.getClass();
							String queueEntryClazzName = queueEntryClazz.getName();
							if (isJavaClass(queueEntryClazzName)) {
								Element queueEntryElement = doc
										.createElement(getFormattedName(queueEntryClazzName));
								element.appendChild(queueEntryElement);
								queueEntryElement.appendChild(doc
										.createTextNode(String.valueOf(queueEntry)));
							} else {
								Element queueEntryElement = doc
										.createElement(getFormattedName(queueEntryClazzName));
								element.appendChild(queueEntryElement);
								convert(queueEntry, doc, queueEntryElement,
										queueEntryClazz);
							}
						}
					}					
				} else if (fieldType.contains("java.util.Set")
						|| fieldType.contains("java.util.HashSet")
						|| fieldType.contains("java.util.TreeSet")
						|| fieldType.contains("java.util.LinkedHashSet")) {
					Method method = clazz.getMethod(getMethodName(fieldName),
							EMPTYPARAMS);
					Set fieldObj = (Set) method.invoke(parent, new Object[0]);
					Element element = doc.createElement(fieldName);
					rootElement.appendChild(element);
					if(fieldObj != null){
						for (Object setEntry : fieldObj) {
							Class setEntryClazz = setEntry.getClass();
							String setEntryClazzName = setEntryClazz.getName();
							if (isJavaClass(setEntryClazzName)) {
								Element setEntryElement = doc
										.createElement(getFormattedName(setEntryClazzName));
								element.appendChild(setEntryElement);
								setEntryElement.appendChild(doc
										.createTextNode(String.valueOf(setEntry)));
							} else {
								Element setEntryElement = doc
										.createElement(getFormattedName(setEntryClazzName));
								element.appendChild(setEntryElement);
								convert(setEntry, doc, setEntryElement,
										setEntryClazz);
							}
						}
					}					
				} else if (fieldType.contains("Map")
						|| fieldType.contains("ConcurrentMap")
						|| fieldType.contains("HashMap")
						|| fieldType.contains("TreeMap")
						|| fieldType.contains("LinkedHashMap")
						|| fieldType.contains("ConcurrentHashMap")) {
					Method method = clazz.getMethod(getMethodName(fieldName),
							EMPTYPARAMS);
					Map<Object, Object> fieldObj = (Map<Object, Object>) method
							.invoke(parent, new Object[0]);
					Element element = doc.createElement(fieldName);
					rootElement.appendChild(element);
					Set<Entry<Object, Object>> entrySet = fieldObj.entrySet();
					if(fieldObj != null){
						for (Entry<Object, Object> entry : entrySet) {
							Object key = entry.getKey();
							Class keyClazz = key.getClass();
							String keyClazzName = keyClazz.getName();

							Object value = entry.getValue();
							Class valueClazz = value.getClass();
							String valueClazzName = valueClazz.getName();

							if (isJavaClass(keyClazzName)) {
								Element keyElement = doc.createElement(String
										.valueOf(key));
								element.appendChild(keyElement);

								if (isJavaClass(valueClazzName)) {
									keyElement.appendChild(doc
											.createTextNode(String.valueOf(value)));
								} else {
									Element valueElement = doc
											.createElement(getFormattedName(valueClazzName));
									keyElement.appendChild(valueElement);
									convert(value, doc, valueElement, valueClazz);
								}
							}

						}
					}
				} else {
					Element element = doc.createElement(fieldName);
					rootElement.appendChild(element);
					
					Method method = clazz.getMethod(getMethodName(fieldName),
							EMPTYPARAMS);
					Object fieldObj = method.invoke(parent, new Object[0]);
					
					convert(fieldObj, doc, element, fieldObj.getClass());
				}

			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param fieldType
	 * @return
	 */
	private static boolean isPrimitiveElement(String fieldType) {
		return fieldType.contains("byte") || fieldType.contains("short")
				|| fieldType.contains("int") || fieldType.contains("long")
				|| fieldType.contains("float") || fieldType.contains("double")
				|| fieldType.contains("boolean") || fieldType.contains("char")
				|| fieldType.contains("String");
	}

	/**
	 * @param fieldType
	 * @return
	 */
	private static boolean isJavaClass(String fieldType) {
		return fieldType.contains("Byte") || fieldType.contains("Short")
				|| fieldType.contains("Integer") || fieldType.contains("Long")
				|| fieldType.contains("Float") || fieldType.contains("Double")
				|| fieldType.contains("Boolean") || fieldType.contains("Char")
				|| fieldType.contains("String");
	}

	private static String getFormattedName(String name) {
		int startIndex = name.lastIndexOf(".");
		if (startIndex > 0) {
			name = name.substring(startIndex + 1, name.length());
		}
		return name;
	}

	private static String getMethodName(String fieldname) {
		String firstChar = fieldname.substring(0, 1).toUpperCase();
		return "get" + firstChar + fieldname.substring(1);
	}

}
