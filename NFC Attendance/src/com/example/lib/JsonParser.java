package com.example.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

	public static String JsonRead(String tableName) {
		// Read json array from "Attendance" table
		return HttpRequestUtils.get(tableName + "/json", null);
	}


	public static JSONArray JsonParse(String jsonWeb) {
		// parse Attendance Json here
		if (jsonWeb.equals(HttpRequestUtils.HTTP_POST_RESPONSE_EXCEPTION)) {
			return null;
		}
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(jsonWeb);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonArray;
	}

	public static String AttendanceCreate(String uid, String code) {
		// create a new record in "Attendance" table
		Map<String, String> map = new HashMap<String, String>();
		map.put("Attendance[uid]", uid);
		map.put("Attendance[code]", code);
		return HttpRequestUtils.post("Attendance/create", map);
	}
	
	public static String AttendanceDCreate(String uid, String code) {
		// create a new record in "Attendance" table
		Map<String, String> map = new HashMap<String, String>();
		map.put("AttendanceD[uid]", uid);
		map.put("AttendanceD[code]", code);
		return HttpRequestUtils.post("AttendanceD/create", map);
	}

	public static String RegistrationCreate(String uid, String cid,
			String attendance) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Registration[uid]", uid);
		map.put("Registration[cid]", cid);
		map.put("Registration[attendance]", attendance);
		return HttpRequestUtils.post("Registration/create", map);
	}
	
	public static String RegistrationDCreate(String uid, String cid,
			String attendance) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("RegistrationD[uid]", uid);
		map.put("RegistrationD[cid]", cid);
		map.put("RegistrationD[attendance]", attendance);
		return HttpRequestUtils.post("RegistrationD/create", map);
	}

	public static String StudentCreate(String uid, String sid, String name) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Student[uid]", uid);
		map.put("Student[sid]", sid);
		map.put("Student[name]", name);
		return HttpRequestUtils.post("Student/create", map);
	}
	
	public static String StudentDCreate(String uid, String sid, String name) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("StudentD[uid]", uid);
		map.put("StudentD[sid]", sid);
		map.put("StudentD[name]", name);
		return HttpRequestUtils.post("StudentD/create", map);
	}

	public static String CourseCreate(String code, String name, String remark) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Course[code]", code);
		map.put("Course[name]", name);
		map.put("Course[remark]", remark);
		return HttpRequestUtils.post("Course/create", map);
	}
	
	public static String CourseDCreate(String code, String name, String remark) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("CourseD[code]", code);
		map.put("CourseD[name]", name);
		map.put("CoursDe[remark]", remark);
		return HttpRequestUtils.post("CourseD/create", map);
	}

	public static String increaseAttValue(String uid, String cid, JSONArray reg) {
		// locate the right record
		JSONObject record = findRecordInReg(uid, cid, reg);
		if (record == null) {
			return "fail";
		}
		int attendance;
		String id = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			attendance = Integer.parseInt(record.getString("attendance")) + 1;
			id = record.getString("id");
			map.put("Registration[uid]", uid);
			map.put("Registration[cid]", cid);
			map.put("Registration[attendance]", attendance + "");
		} catch (NumberFormatException e) {
			System.out.println("get attendance failed");
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return HttpRequestUtils.post("Registration/update/" + id, map);
	}

	public static String decreaseAttValue(String uid, String cid, JSONArray reg) {
		// locate the right record
		JSONObject record = findRecordInReg(uid, cid, reg);
		try {
			if (Integer.parseInt(record.getString("attendance")) <= 0) {
				return "fail";
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		// update the value
		int attendance;
		String id = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			attendance = Integer.parseInt(record.getString("attendance")) - 1;
			id = record.getString("id");
			map.put("RegistrationD[uid]", uid);
			map.put("RegistrationD[cid]", cid);
			map.put("RegistrationD[attendance]", attendance + "");
		} catch (NumberFormatException e) {
			System.out.println("get attendance failed");
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return HttpRequestUtils.post("RegistrationD/update/" + id, map);
	}
	/**
	 * return all the course this uid registered
	 * ex: { #courses, coursecode1, coursecode2, coursecode3}
	 * @param uid
	 * @param reg
	 * @return
	 */
	public static String[] findCourseInReg(String uid, JSONArray reg) {
		String[] courses = new String[reg.length()+1];// 0,1,......19
		int counter = 0, index = 1;
		try {
			JSONObject item = null;
			for (int i = 0; i < reg.length(); i++) {
				if (reg.getJSONObject(i).getString("uid").equals(uid)) {
					item = reg.getJSONObject(i);
					counter++;
					courses[index++] = item.getString("cid");
				}
				// if(counter == reg.length()){
				// break;
				// }
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		courses[0] = counter + "";

		return courses;
	}

	public static String[] returnAllCourse(JSONArray courseTable) {
		String[] courses = new String[courseTable.length()];
		try {
			for (int i = 0; i < courseTable.length(); i++) {
				courses[i] = courseTable.getJSONObject(i).getString("code");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return courses;
	}
	
	public static String[] returnAllStudent(JSONArray studentTable) {
		String[] students = new String[studentTable.length()];
		try {
			for (int i = 0; i < studentTable.length(); i++) {
				students[i] = studentTable.getJSONObject(i).getString("sid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return students;
	}

	/**
	 * return a string array include all the students' uids who has registered
	 * the 'course' used in spinner
	 * 
	 * @param regTable
	 * @param course
	 * @return []studentUid
	 */
	public static String[] returnRegedStudent(JSONArray regTable, String course) {
		String[] students = null;
		JSONObject cStudent = null;
		int aUID = 0, cSID = 0;
		try {
			// get how many student for the length for students array
			for (int i = 0; i < regTable.length(); i++) {
				if (regTable.getJSONObject(i).getString("cid").equals(course)) {
					aUID++;
				}
			}
			if (aUID == 0) {
				return null;
			}
			// build students
			students = new String[aUID];
			// students[0] = "All";
			for (int i = 0; cSID < aUID && i < regTable.length(); i++) {
				cStudent = regTable.getJSONObject(i);
				if (cStudent.getString("cid").equals(course)) {
					students[cSID] = cStudent.getString("uid");
					cSID++;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return students;
	}

	public static JSONObject findRecordInReg(String uid, String cid,
			JSONArray reg) {
		JSONObject record = null;
		int i;
		try {
			for (i = 0; i < reg.length(); i++) {
				if (reg.getJSONObject(i).getString("uid").equals(uid)) {
					if (reg.getJSONObject(i).getString("cid").equals(cid)) {
						record = reg.getJSONObject(i);
						break;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return record;
	}

	public static JSONObject findStudentByUid(String uid, JSONArray table) {
		JSONObject student = null;
		if (!isCardExisted(uid, table)) {
			return student;
		}
		try {
			for (int i = 0; i < table.length(); i++) {
				if (uid.equals(table.getJSONObject(i).getString("uid"))) {
					student = table.getJSONObject(i);
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return student;
	}

	public static String[] findStudentUIDsBySid(String sid, JSONArray table) {
		ArrayList<String> students = new ArrayList<String>();
		try {
			for (int i = 0; i < table.length(); i++) {
				if (sid.equals(table.getJSONObject(i).getString("sid"))) {
					students.add(table.getJSONObject(i).getString("uid"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String[] returnString = new String[students.size()];
		for (int i = 0; i < students.size(); i++) {
			returnString[i] = students.get(i);
		}
		return returnString;
	}

	public static JSONObject findCourseByCode(String code, JSONArray table) {
		JSONObject course = null;
		try {
			for (int i = 0; i < table.length(); i++) {
				if (code.equals(table.getJSONObject(i).getString("code"))) {
					course = table.getJSONObject(i);
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return course;
	}

	/**
	 * check if this card has been registered for a student
	 * 
	 * @param uid
	 * @param table
	 * @return true---exist; false --- not yet registered
	 */
	public static boolean isCardExisted(String uid, JSONArray table) {
		// check if this card has been registered for a student
		try {
			for (int i = 0; i < table.length(); i++) {
				if (uid.equals(table.getJSONObject(i).getString("uid"))) {
					return true;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isCourseExisted(String code, JSONArray table) {
		// check if this course is existed
		try {
			for (int i = 0; i < table.length(); i++) {
				if (code.equals(table.getJSONObject(i).getString("code"))) {
					return true;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * just for help, don't use JSON at all
	 * 
	 * @param code
	 * @param table
	 * @return
	 */
	public static boolean isRecordExisted(String sid, String[] records) {
		for (int i = 0; i < records.length && records[i] != null; i++) {
			if (sid.equals(records[i])) {
				return true;
			}
		}
		return false;
	}

//	public static String update(String uid, String cid) {
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("Registration[uid]", uid);
//		map.put("Registration[cid]", cid);
//		map.put("Registration[attendance]", "06");
//
//		return HttpRequestUtils.post("Registration/update/13", map);
//
//	}

}
