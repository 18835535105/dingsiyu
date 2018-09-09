package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class StudentExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StudentExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        protected void addCriterionForJDBCDate(String condition, Date value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value.getTime()), property);
        }

        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();
            Iterator<Date> iter = values.iterator();
            while (iter.hasNext()) {
                dateList.add(new java.sql.Date(iter.next().getTime()));
            }
            addCriterion(condition, dateList, property);
        }

        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andAccountIsNull() {
            addCriterion("account is null");
            return (Criteria) this;
        }

        public Criteria andAccountIsNotNull() {
            addCriterion("account is not null");
            return (Criteria) this;
        }

        public Criteria andAccountEqualTo(String value) {
            addCriterion("account =", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountNotEqualTo(String value) {
            addCriterion("account <>", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountGreaterThan(String value) {
            addCriterion("account >", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountGreaterThanOrEqualTo(String value) {
            addCriterion("account >=", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountLessThan(String value) {
            addCriterion("account <", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountLessThanOrEqualTo(String value) {
            addCriterion("account <=", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountLike(String value) {
            addCriterion("account like", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountNotLike(String value) {
            addCriterion("account not like", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountIn(List<String> values) {
            addCriterion("account in", values, "account");
            return (Criteria) this;
        }

        public Criteria andAccountNotIn(List<String> values) {
            addCriterion("account not in", values, "account");
            return (Criteria) this;
        }

        public Criteria andAccountBetween(String value1, String value2) {
            addCriterion("account between", value1, value2, "account");
            return (Criteria) this;
        }

        public Criteria andAccountNotBetween(String value1, String value2) {
            addCriterion("account not between", value1, value2, "account");
            return (Criteria) this;
        }

        public Criteria andPasswordIsNull() {
            addCriterion("password is null");
            return (Criteria) this;
        }

        public Criteria andPasswordIsNotNull() {
            addCriterion("password is not null");
            return (Criteria) this;
        }

        public Criteria andPasswordEqualTo(String value) {
            addCriterion("password =", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotEqualTo(String value) {
            addCriterion("password <>", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordGreaterThan(String value) {
            addCriterion("password >", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordGreaterThanOrEqualTo(String value) {
            addCriterion("password >=", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLessThan(String value) {
            addCriterion("password <", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLessThanOrEqualTo(String value) {
            addCriterion("password <=", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLike(String value) {
            addCriterion("password like", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotLike(String value) {
            addCriterion("password not like", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordIn(List<String> values) {
            addCriterion("password in", values, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotIn(List<String> values) {
            addCriterion("password not in", values, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordBetween(String value1, String value2) {
            addCriterion("password between", value1, value2, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotBetween(String value1, String value2) {
            addCriterion("password not between", value1, value2, "password");
            return (Criteria) this;
        }

        public Criteria andAccountTimeIsNull() {
            addCriterion("account_time is null");
            return (Criteria) this;
        }

        public Criteria andAccountTimeIsNotNull() {
            addCriterion("account_time is not null");
            return (Criteria) this;
        }

        public Criteria andAccountTimeEqualTo(Date value) {
            addCriterion("account_time =", value, "accountTime");
            return (Criteria) this;
        }

        public Criteria andAccountTimeNotEqualTo(Date value) {
            addCriterion("account_time <>", value, "accountTime");
            return (Criteria) this;
        }

        public Criteria andAccountTimeGreaterThan(Date value) {
            addCriterion("account_time >", value, "accountTime");
            return (Criteria) this;
        }

        public Criteria andAccountTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("account_time >=", value, "accountTime");
            return (Criteria) this;
        }

        public Criteria andAccountTimeLessThan(Date value) {
            addCriterion("account_time <", value, "accountTime");
            return (Criteria) this;
        }

        public Criteria andAccountTimeLessThanOrEqualTo(Date value) {
            addCriterion("account_time <=", value, "accountTime");
            return (Criteria) this;
        }

        public Criteria andAccountTimeIn(List<Date> values) {
            addCriterion("account_time in", values, "accountTime");
            return (Criteria) this;
        }

        public Criteria andAccountTimeNotIn(List<Date> values) {
            addCriterion("account_time not in", values, "accountTime");
            return (Criteria) this;
        }

        public Criteria andAccountTimeBetween(Date value1, Date value2) {
            addCriterion("account_time between", value1, value2, "accountTime");
            return (Criteria) this;
        }

        public Criteria andAccountTimeNotBetween(Date value1, Date value2) {
            addCriterion("account_time not between", value1, value2, "accountTime");
            return (Criteria) this;
        }

        public Criteria andProvinceIsNull() {
            addCriterion("province is null");
            return (Criteria) this;
        }

        public Criteria andProvinceIsNotNull() {
            addCriterion("province is not null");
            return (Criteria) this;
        }

        public Criteria andProvinceEqualTo(String value) {
            addCriterion("province =", value, "province");
            return (Criteria) this;
        }

        public Criteria andProvinceNotEqualTo(String value) {
            addCriterion("province <>", value, "province");
            return (Criteria) this;
        }

        public Criteria andProvinceGreaterThan(String value) {
            addCriterion("province >", value, "province");
            return (Criteria) this;
        }

        public Criteria andProvinceGreaterThanOrEqualTo(String value) {
            addCriterion("province >=", value, "province");
            return (Criteria) this;
        }

        public Criteria andProvinceLessThan(String value) {
            addCriterion("province <", value, "province");
            return (Criteria) this;
        }

        public Criteria andProvinceLessThanOrEqualTo(String value) {
            addCriterion("province <=", value, "province");
            return (Criteria) this;
        }

        public Criteria andProvinceLike(String value) {
            addCriterion("province like", value, "province");
            return (Criteria) this;
        }

        public Criteria andProvinceNotLike(String value) {
            addCriterion("province not like", value, "province");
            return (Criteria) this;
        }

        public Criteria andProvinceIn(List<String> values) {
            addCriterion("province in", values, "province");
            return (Criteria) this;
        }

        public Criteria andProvinceNotIn(List<String> values) {
            addCriterion("province not in", values, "province");
            return (Criteria) this;
        }

        public Criteria andProvinceBetween(String value1, String value2) {
            addCriterion("province between", value1, value2, "province");
            return (Criteria) this;
        }

        public Criteria andProvinceNotBetween(String value1, String value2) {
            addCriterion("province not between", value1, value2, "province");
            return (Criteria) this;
        }

        public Criteria andCityIsNull() {
            addCriterion("city is null");
            return (Criteria) this;
        }

        public Criteria andCityIsNotNull() {
            addCriterion("city is not null");
            return (Criteria) this;
        }

        public Criteria andCityEqualTo(String value) {
            addCriterion("city =", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityNotEqualTo(String value) {
            addCriterion("city <>", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityGreaterThan(String value) {
            addCriterion("city >", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityGreaterThanOrEqualTo(String value) {
            addCriterion("city >=", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityLessThan(String value) {
            addCriterion("city <", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityLessThanOrEqualTo(String value) {
            addCriterion("city <=", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityLike(String value) {
            addCriterion("city like", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityNotLike(String value) {
            addCriterion("city not like", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityIn(List<String> values) {
            addCriterion("city in", values, "city");
            return (Criteria) this;
        }

        public Criteria andCityNotIn(List<String> values) {
            addCriterion("city not in", values, "city");
            return (Criteria) this;
        }

        public Criteria andCityBetween(String value1, String value2) {
            addCriterion("city between", value1, value2, "city");
            return (Criteria) this;
        }

        public Criteria andCityNotBetween(String value1, String value2) {
            addCriterion("city not between", value1, value2, "city");
            return (Criteria) this;
        }

        public Criteria andAreaIsNull() {
            addCriterion("area is null");
            return (Criteria) this;
        }

        public Criteria andAreaIsNotNull() {
            addCriterion("area is not null");
            return (Criteria) this;
        }

        public Criteria andAreaEqualTo(String value) {
            addCriterion("area =", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaNotEqualTo(String value) {
            addCriterion("area <>", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaGreaterThan(String value) {
            addCriterion("area >", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaGreaterThanOrEqualTo(String value) {
            addCriterion("area >=", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaLessThan(String value) {
            addCriterion("area <", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaLessThanOrEqualTo(String value) {
            addCriterion("area <=", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaLike(String value) {
            addCriterion("area like", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaNotLike(String value) {
            addCriterion("area not like", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaIn(List<String> values) {
            addCriterion("area in", values, "area");
            return (Criteria) this;
        }

        public Criteria andAreaNotIn(List<String> values) {
            addCriterion("area not in", values, "area");
            return (Criteria) this;
        }

        public Criteria andAreaBetween(String value1, String value2) {
            addCriterion("area between", value1, value2, "area");
            return (Criteria) this;
        }

        public Criteria andAreaNotBetween(String value1, String value2) {
            addCriterion("area not between", value1, value2, "area");
            return (Criteria) this;
        }

        public Criteria andSchoolNameIsNull() {
            addCriterion("school_name is null");
            return (Criteria) this;
        }

        public Criteria andSchoolNameIsNotNull() {
            addCriterion("school_name is not null");
            return (Criteria) this;
        }

        public Criteria andSchoolNameEqualTo(String value) {
            addCriterion("school_name =", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameNotEqualTo(String value) {
            addCriterion("school_name <>", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameGreaterThan(String value) {
            addCriterion("school_name >", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameGreaterThanOrEqualTo(String value) {
            addCriterion("school_name >=", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameLessThan(String value) {
            addCriterion("school_name <", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameLessThanOrEqualTo(String value) {
            addCriterion("school_name <=", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameLike(String value) {
            addCriterion("school_name like", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameNotLike(String value) {
            addCriterion("school_name not like", value, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameIn(List<String> values) {
            addCriterion("school_name in", values, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameNotIn(List<String> values) {
            addCriterion("school_name not in", values, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameBetween(String value1, String value2) {
            addCriterion("school_name between", value1, value2, "schoolName");
            return (Criteria) this;
        }

        public Criteria andSchoolNameNotBetween(String value1, String value2) {
            addCriterion("school_name not between", value1, value2, "schoolName");
            return (Criteria) this;
        }

        public Criteria andStudentNameIsNull() {
            addCriterion("student_name is null");
            return (Criteria) this;
        }

        public Criteria andStudentNameIsNotNull() {
            addCriterion("student_name is not null");
            return (Criteria) this;
        }

        public Criteria andStudentNameEqualTo(String value) {
            addCriterion("student_name =", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameNotEqualTo(String value) {
            addCriterion("student_name <>", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameGreaterThan(String value) {
            addCriterion("student_name >", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameGreaterThanOrEqualTo(String value) {
            addCriterion("student_name >=", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameLessThan(String value) {
            addCriterion("student_name <", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameLessThanOrEqualTo(String value) {
            addCriterion("student_name <=", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameLike(String value) {
            addCriterion("student_name like", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameNotLike(String value) {
            addCriterion("student_name not like", value, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameIn(List<String> values) {
            addCriterion("student_name in", values, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameNotIn(List<String> values) {
            addCriterion("student_name not in", values, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameBetween(String value1, String value2) {
            addCriterion("student_name between", value1, value2, "studentName");
            return (Criteria) this;
        }

        public Criteria andStudentNameNotBetween(String value1, String value2) {
            addCriterion("student_name not between", value1, value2, "studentName");
            return (Criteria) this;
        }

        public Criteria andSexIsNull() {
            addCriterion("sex is null");
            return (Criteria) this;
        }

        public Criteria andSexIsNotNull() {
            addCriterion("sex is not null");
            return (Criteria) this;
        }

        public Criteria andSexEqualTo(Integer value) {
            addCriterion("sex =", value, "sex");
            return (Criteria) this;
        }

        public Criteria andSexNotEqualTo(Integer value) {
            addCriterion("sex <>", value, "sex");
            return (Criteria) this;
        }

        public Criteria andSexGreaterThan(Integer value) {
            addCriterion("sex >", value, "sex");
            return (Criteria) this;
        }

        public Criteria andSexGreaterThanOrEqualTo(Integer value) {
            addCriterion("sex >=", value, "sex");
            return (Criteria) this;
        }

        public Criteria andSexLessThan(Integer value) {
            addCriterion("sex <", value, "sex");
            return (Criteria) this;
        }

        public Criteria andSexLessThanOrEqualTo(Integer value) {
            addCriterion("sex <=", value, "sex");
            return (Criteria) this;
        }

        public Criteria andSexIn(List<Integer> values) {
            addCriterion("sex in", values, "sex");
            return (Criteria) this;
        }

        public Criteria andSexNotIn(List<Integer> values) {
            addCriterion("sex not in", values, "sex");
            return (Criteria) this;
        }

        public Criteria andSexBetween(Integer value1, Integer value2) {
            addCriterion("sex between", value1, value2, "sex");
            return (Criteria) this;
        }

        public Criteria andSexNotBetween(Integer value1, Integer value2) {
            addCriterion("sex not between", value1, value2, "sex");
            return (Criteria) this;
        }

        public Criteria andNicknameIsNull() {
            addCriterion("nickname is null");
            return (Criteria) this;
        }

        public Criteria andNicknameIsNotNull() {
            addCriterion("nickname is not null");
            return (Criteria) this;
        }

        public Criteria andNicknameEqualTo(String value) {
            addCriterion("nickname =", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameNotEqualTo(String value) {
            addCriterion("nickname <>", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameGreaterThan(String value) {
            addCriterion("nickname >", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameGreaterThanOrEqualTo(String value) {
            addCriterion("nickname >=", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameLessThan(String value) {
            addCriterion("nickname <", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameLessThanOrEqualTo(String value) {
            addCriterion("nickname <=", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameLike(String value) {
            addCriterion("nickname like", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameNotLike(String value) {
            addCriterion("nickname not like", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameIn(List<String> values) {
            addCriterion("nickname in", values, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameNotIn(List<String> values) {
            addCriterion("nickname not in", values, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameBetween(String value1, String value2) {
            addCriterion("nickname between", value1, value2, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameNotBetween(String value1, String value2) {
            addCriterion("nickname not between", value1, value2, "nickname");
            return (Criteria) this;
        }

        public Criteria andBirthDateIsNull() {
            addCriterion("birth_date is null");
            return (Criteria) this;
        }

        public Criteria andBirthDateIsNotNull() {
            addCriterion("birth_date is not null");
            return (Criteria) this;
        }

        public Criteria andBirthDateEqualTo(Date value) {
            addCriterionForJDBCDate("birth_date =", value, "birthDate");
            return (Criteria) this;
        }

        public Criteria andBirthDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("birth_date <>", value, "birthDate");
            return (Criteria) this;
        }

        public Criteria andBirthDateGreaterThan(Date value) {
            addCriterionForJDBCDate("birth_date >", value, "birthDate");
            return (Criteria) this;
        }

        public Criteria andBirthDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("birth_date >=", value, "birthDate");
            return (Criteria) this;
        }

        public Criteria andBirthDateLessThan(Date value) {
            addCriterionForJDBCDate("birth_date <", value, "birthDate");
            return (Criteria) this;
        }

        public Criteria andBirthDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("birth_date <=", value, "birthDate");
            return (Criteria) this;
        }

        public Criteria andBirthDateIn(List<Date> values) {
            addCriterionForJDBCDate("birth_date in", values, "birthDate");
            return (Criteria) this;
        }

        public Criteria andBirthDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("birth_date not in", values, "birthDate");
            return (Criteria) this;
        }

        public Criteria andBirthDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("birth_date between", value1, value2, "birthDate");
            return (Criteria) this;
        }

        public Criteria andBirthDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("birth_date not between", value1, value2, "birthDate");
            return (Criteria) this;
        }

        public Criteria andRegisterDateIsNull() {
            addCriterion("register_date is null");
            return (Criteria) this;
        }

        public Criteria andRegisterDateIsNotNull() {
            addCriterion("register_date is not null");
            return (Criteria) this;
        }

        public Criteria andRegisterDateEqualTo(Date value) {
            addCriterion("register_date =", value, "registerDate");
            return (Criteria) this;
        }

        public Criteria andRegisterDateNotEqualTo(Date value) {
            addCriterion("register_date <>", value, "registerDate");
            return (Criteria) this;
        }

        public Criteria andRegisterDateGreaterThan(Date value) {
            addCriterion("register_date >", value, "registerDate");
            return (Criteria) this;
        }

        public Criteria andRegisterDateGreaterThanOrEqualTo(Date value) {
            addCriterion("register_date >=", value, "registerDate");
            return (Criteria) this;
        }

        public Criteria andRegisterDateLessThan(Date value) {
            addCriterion("register_date <", value, "registerDate");
            return (Criteria) this;
        }

        public Criteria andRegisterDateLessThanOrEqualTo(Date value) {
            addCriterion("register_date <=", value, "registerDate");
            return (Criteria) this;
        }

        public Criteria andRegisterDateIn(List<Date> values) {
            addCriterion("register_date in", values, "registerDate");
            return (Criteria) this;
        }

        public Criteria andRegisterDateNotIn(List<Date> values) {
            addCriterion("register_date not in", values, "registerDate");
            return (Criteria) this;
        }

        public Criteria andRegisterDateBetween(Date value1, Date value2) {
            addCriterion("register_date between", value1, value2, "registerDate");
            return (Criteria) this;
        }

        public Criteria andRegisterDateNotBetween(Date value1, Date value2) {
            addCriterion("register_date not between", value1, value2, "registerDate");
            return (Criteria) this;
        }

        public Criteria andGradeIsNull() {
            addCriterion("grade is null");
            return (Criteria) this;
        }

        public Criteria andGradeIsNotNull() {
            addCriterion("grade is not null");
            return (Criteria) this;
        }

        public Criteria andGradeEqualTo(String value) {
            addCriterion("grade =", value, "grade");
            return (Criteria) this;
        }

        public Criteria andGradeNotEqualTo(String value) {
            addCriterion("grade <>", value, "grade");
            return (Criteria) this;
        }

        public Criteria andGradeGreaterThan(String value) {
            addCriterion("grade >", value, "grade");
            return (Criteria) this;
        }

        public Criteria andGradeGreaterThanOrEqualTo(String value) {
            addCriterion("grade >=", value, "grade");
            return (Criteria) this;
        }

        public Criteria andGradeLessThan(String value) {
            addCriterion("grade <", value, "grade");
            return (Criteria) this;
        }

        public Criteria andGradeLessThanOrEqualTo(String value) {
            addCriterion("grade <=", value, "grade");
            return (Criteria) this;
        }

        public Criteria andGradeLike(String value) {
            addCriterion("grade like", value, "grade");
            return (Criteria) this;
        }

        public Criteria andGradeNotLike(String value) {
            addCriterion("grade not like", value, "grade");
            return (Criteria) this;
        }

        public Criteria andGradeIn(List<String> values) {
            addCriterion("grade in", values, "grade");
            return (Criteria) this;
        }

        public Criteria andGradeNotIn(List<String> values) {
            addCriterion("grade not in", values, "grade");
            return (Criteria) this;
        }

        public Criteria andGradeBetween(String value1, String value2) {
            addCriterion("grade between", value1, value2, "grade");
            return (Criteria) this;
        }

        public Criteria andGradeNotBetween(String value1, String value2) {
            addCriterion("grade not between", value1, value2, "grade");
            return (Criteria) this;
        }

        public Criteria andSquadIsNull() {
            addCriterion("squad is null");
            return (Criteria) this;
        }

        public Criteria andSquadIsNotNull() {
            addCriterion("squad is not null");
            return (Criteria) this;
        }

        public Criteria andSquadEqualTo(String value) {
            addCriterion("squad =", value, "squad");
            return (Criteria) this;
        }

        public Criteria andSquadNotEqualTo(String value) {
            addCriterion("squad <>", value, "squad");
            return (Criteria) this;
        }

        public Criteria andSquadGreaterThan(String value) {
            addCriterion("squad >", value, "squad");
            return (Criteria) this;
        }

        public Criteria andSquadGreaterThanOrEqualTo(String value) {
            addCriterion("squad >=", value, "squad");
            return (Criteria) this;
        }

        public Criteria andSquadLessThan(String value) {
            addCriterion("squad <", value, "squad");
            return (Criteria) this;
        }

        public Criteria andSquadLessThanOrEqualTo(String value) {
            addCriterion("squad <=", value, "squad");
            return (Criteria) this;
        }

        public Criteria andSquadLike(String value) {
            addCriterion("squad like", value, "squad");
            return (Criteria) this;
        }

        public Criteria andSquadNotLike(String value) {
            addCriterion("squad not like", value, "squad");
            return (Criteria) this;
        }

        public Criteria andSquadIn(List<String> values) {
            addCriterion("squad in", values, "squad");
            return (Criteria) this;
        }

        public Criteria andSquadNotIn(List<String> values) {
            addCriterion("squad not in", values, "squad");
            return (Criteria) this;
        }

        public Criteria andSquadBetween(String value1, String value2) {
            addCriterion("squad between", value1, value2, "squad");
            return (Criteria) this;
        }

        public Criteria andSquadNotBetween(String value1, String value2) {
            addCriterion("squad not between", value1, value2, "squad");
            return (Criteria) this;
        }

        public Criteria andWishIsNull() {
            addCriterion("wish is null");
            return (Criteria) this;
        }

        public Criteria andWishIsNotNull() {
            addCriterion("wish is not null");
            return (Criteria) this;
        }

        public Criteria andWishEqualTo(String value) {
            addCriterion("wish =", value, "wish");
            return (Criteria) this;
        }

        public Criteria andWishNotEqualTo(String value) {
            addCriterion("wish <>", value, "wish");
            return (Criteria) this;
        }

        public Criteria andWishGreaterThan(String value) {
            addCriterion("wish >", value, "wish");
            return (Criteria) this;
        }

        public Criteria andWishGreaterThanOrEqualTo(String value) {
            addCriterion("wish >=", value, "wish");
            return (Criteria) this;
        }

        public Criteria andWishLessThan(String value) {
            addCriterion("wish <", value, "wish");
            return (Criteria) this;
        }

        public Criteria andWishLessThanOrEqualTo(String value) {
            addCriterion("wish <=", value, "wish");
            return (Criteria) this;
        }

        public Criteria andWishLike(String value) {
            addCriterion("wish like", value, "wish");
            return (Criteria) this;
        }

        public Criteria andWishNotLike(String value) {
            addCriterion("wish not like", value, "wish");
            return (Criteria) this;
        }

        public Criteria andWishIn(List<String> values) {
            addCriterion("wish in", values, "wish");
            return (Criteria) this;
        }

        public Criteria andWishNotIn(List<String> values) {
            addCriterion("wish not in", values, "wish");
            return (Criteria) this;
        }

        public Criteria andWishBetween(String value1, String value2) {
            addCriterion("wish between", value1, value2, "wish");
            return (Criteria) this;
        }

        public Criteria andWishNotBetween(String value1, String value2) {
            addCriterion("wish not between", value1, value2, "wish");
            return (Criteria) this;
        }

        public Criteria andPhoneIsNull() {
            addCriterion("phone is null");
            return (Criteria) this;
        }

        public Criteria andPhoneIsNotNull() {
            addCriterion("phone is not null");
            return (Criteria) this;
        }

        public Criteria andPhoneEqualTo(String value) {
            addCriterion("phone =", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneNotEqualTo(String value) {
            addCriterion("phone <>", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneGreaterThan(String value) {
            addCriterion("phone >", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneGreaterThanOrEqualTo(String value) {
            addCriterion("phone >=", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneLessThan(String value) {
            addCriterion("phone <", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneLessThanOrEqualTo(String value) {
            addCriterion("phone <=", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneLike(String value) {
            addCriterion("phone like", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneNotLike(String value) {
            addCriterion("phone not like", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneIn(List<String> values) {
            addCriterion("phone in", values, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneNotIn(List<String> values) {
            addCriterion("phone not in", values, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneBetween(String value1, String value2) {
            addCriterion("phone between", value1, value2, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneNotBetween(String value1, String value2) {
            addCriterion("phone not between", value1, value2, "phone");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneIsNull() {
            addCriterion("patriarch_phone is null");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneIsNotNull() {
            addCriterion("patriarch_phone is not null");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneEqualTo(String value) {
            addCriterion("patriarch_phone =", value, "patriarchPhone");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneNotEqualTo(String value) {
            addCriterion("patriarch_phone <>", value, "patriarchPhone");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneGreaterThan(String value) {
            addCriterion("patriarch_phone >", value, "patriarchPhone");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneGreaterThanOrEqualTo(String value) {
            addCriterion("patriarch_phone >=", value, "patriarchPhone");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneLessThan(String value) {
            addCriterion("patriarch_phone <", value, "patriarchPhone");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneLessThanOrEqualTo(String value) {
            addCriterion("patriarch_phone <=", value, "patriarchPhone");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneLike(String value) {
            addCriterion("patriarch_phone like", value, "patriarchPhone");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneNotLike(String value) {
            addCriterion("patriarch_phone not like", value, "patriarchPhone");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneIn(List<String> values) {
            addCriterion("patriarch_phone in", values, "patriarchPhone");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneNotIn(List<String> values) {
            addCriterion("patriarch_phone not in", values, "patriarchPhone");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneBetween(String value1, String value2) {
            addCriterion("patriarch_phone between", value1, value2, "patriarchPhone");
            return (Criteria) this;
        }

        public Criteria andPatriarchPhoneNotBetween(String value1, String value2) {
            addCriterion("patriarch_phone not between", value1, value2, "patriarchPhone");
            return (Criteria) this;
        }

        public Criteria andMailIsNull() {
            addCriterion("mail is null");
            return (Criteria) this;
        }

        public Criteria andMailIsNotNull() {
            addCriterion("mail is not null");
            return (Criteria) this;
        }

        public Criteria andMailEqualTo(String value) {
            addCriterion("mail =", value, "mail");
            return (Criteria) this;
        }

        public Criteria andMailNotEqualTo(String value) {
            addCriterion("mail <>", value, "mail");
            return (Criteria) this;
        }

        public Criteria andMailGreaterThan(String value) {
            addCriterion("mail >", value, "mail");
            return (Criteria) this;
        }

        public Criteria andMailGreaterThanOrEqualTo(String value) {
            addCriterion("mail >=", value, "mail");
            return (Criteria) this;
        }

        public Criteria andMailLessThan(String value) {
            addCriterion("mail <", value, "mail");
            return (Criteria) this;
        }

        public Criteria andMailLessThanOrEqualTo(String value) {
            addCriterion("mail <=", value, "mail");
            return (Criteria) this;
        }

        public Criteria andMailLike(String value) {
            addCriterion("mail like", value, "mail");
            return (Criteria) this;
        }

        public Criteria andMailNotLike(String value) {
            addCriterion("mail not like", value, "mail");
            return (Criteria) this;
        }

        public Criteria andMailIn(List<String> values) {
            addCriterion("mail in", values, "mail");
            return (Criteria) this;
        }

        public Criteria andMailNotIn(List<String> values) {
            addCriterion("mail not in", values, "mail");
            return (Criteria) this;
        }

        public Criteria andMailBetween(String value1, String value2) {
            addCriterion("mail between", value1, value2, "mail");
            return (Criteria) this;
        }

        public Criteria andMailNotBetween(String value1, String value2) {
            addCriterion("mail not between", value1, value2, "mail");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andHeadUrlIsNull() {
            addCriterion("head_url is null");
            return (Criteria) this;
        }

        public Criteria andHeadUrlIsNotNull() {
            addCriterion("head_url is not null");
            return (Criteria) this;
        }

        public Criteria andHeadUrlEqualTo(String value) {
            addCriterion("head_url =", value, "headUrl");
            return (Criteria) this;
        }

        public Criteria andHeadUrlNotEqualTo(String value) {
            addCriterion("head_url <>", value, "headUrl");
            return (Criteria) this;
        }

        public Criteria andHeadUrlGreaterThan(String value) {
            addCriterion("head_url >", value, "headUrl");
            return (Criteria) this;
        }

        public Criteria andHeadUrlGreaterThanOrEqualTo(String value) {
            addCriterion("head_url >=", value, "headUrl");
            return (Criteria) this;
        }

        public Criteria andHeadUrlLessThan(String value) {
            addCriterion("head_url <", value, "headUrl");
            return (Criteria) this;
        }

        public Criteria andHeadUrlLessThanOrEqualTo(String value) {
            addCriterion("head_url <=", value, "headUrl");
            return (Criteria) this;
        }

        public Criteria andHeadUrlLike(String value) {
            addCriterion("head_url like", value, "headUrl");
            return (Criteria) this;
        }

        public Criteria andHeadUrlNotLike(String value) {
            addCriterion("head_url not like", value, "headUrl");
            return (Criteria) this;
        }

        public Criteria andHeadUrlIn(List<String> values) {
            addCriterion("head_url in", values, "headUrl");
            return (Criteria) this;
        }

        public Criteria andHeadUrlNotIn(List<String> values) {
            addCriterion("head_url not in", values, "headUrl");
            return (Criteria) this;
        }

        public Criteria andHeadUrlBetween(String value1, String value2) {
            addCriterion("head_url between", value1, value2, "headUrl");
            return (Criteria) this;
        }

        public Criteria andHeadUrlNotBetween(String value1, String value2) {
            addCriterion("head_url not between", value1, value2, "headUrl");
            return (Criteria) this;
        }

        public Criteria andHeadNameIsNull() {
            addCriterion("head_name is null");
            return (Criteria) this;
        }

        public Criteria andHeadNameIsNotNull() {
            addCriterion("head_name is not null");
            return (Criteria) this;
        }

        public Criteria andHeadNameEqualTo(String value) {
            addCriterion("head_name =", value, "headName");
            return (Criteria) this;
        }

        public Criteria andHeadNameNotEqualTo(String value) {
            addCriterion("head_name <>", value, "headName");
            return (Criteria) this;
        }

        public Criteria andHeadNameGreaterThan(String value) {
            addCriterion("head_name >", value, "headName");
            return (Criteria) this;
        }

        public Criteria andHeadNameGreaterThanOrEqualTo(String value) {
            addCriterion("head_name >=", value, "headName");
            return (Criteria) this;
        }

        public Criteria andHeadNameLessThan(String value) {
            addCriterion("head_name <", value, "headName");
            return (Criteria) this;
        }

        public Criteria andHeadNameLessThanOrEqualTo(String value) {
            addCriterion("head_name <=", value, "headName");
            return (Criteria) this;
        }

        public Criteria andHeadNameLike(String value) {
            addCriterion("head_name like", value, "headName");
            return (Criteria) this;
        }

        public Criteria andHeadNameNotLike(String value) {
            addCriterion("head_name not like", value, "headName");
            return (Criteria) this;
        }

        public Criteria andHeadNameIn(List<String> values) {
            addCriterion("head_name in", values, "headName");
            return (Criteria) this;
        }

        public Criteria andHeadNameNotIn(List<String> values) {
            addCriterion("head_name not in", values, "headName");
            return (Criteria) this;
        }

        public Criteria andHeadNameBetween(String value1, String value2) {
            addCriterion("head_name between", value1, value2, "headName");
            return (Criteria) this;
        }

        public Criteria andHeadNameNotBetween(String value1, String value2) {
            addCriterion("head_name not between", value1, value2, "headName");
            return (Criteria) this;
        }

        public Criteria andPartUrlIsNull() {
            addCriterion("part_url is null");
            return (Criteria) this;
        }

        public Criteria andPartUrlIsNotNull() {
            addCriterion("part_url is not null");
            return (Criteria) this;
        }

        public Criteria andPartUrlEqualTo(String value) {
            addCriterion("part_url =", value, "partUrl");
            return (Criteria) this;
        }

        public Criteria andPartUrlNotEqualTo(String value) {
            addCriterion("part_url <>", value, "partUrl");
            return (Criteria) this;
        }

        public Criteria andPartUrlGreaterThan(String value) {
            addCriterion("part_url >", value, "partUrl");
            return (Criteria) this;
        }

        public Criteria andPartUrlGreaterThanOrEqualTo(String value) {
            addCriterion("part_url >=", value, "partUrl");
            return (Criteria) this;
        }

        public Criteria andPartUrlLessThan(String value) {
            addCriterion("part_url <", value, "partUrl");
            return (Criteria) this;
        }

        public Criteria andPartUrlLessThanOrEqualTo(String value) {
            addCriterion("part_url <=", value, "partUrl");
            return (Criteria) this;
        }

        public Criteria andPartUrlLike(String value) {
            addCriterion("part_url like", value, "partUrl");
            return (Criteria) this;
        }

        public Criteria andPartUrlNotLike(String value) {
            addCriterion("part_url not like", value, "partUrl");
            return (Criteria) this;
        }

        public Criteria andPartUrlIn(List<String> values) {
            addCriterion("part_url in", values, "partUrl");
            return (Criteria) this;
        }

        public Criteria andPartUrlNotIn(List<String> values) {
            addCriterion("part_url not in", values, "partUrl");
            return (Criteria) this;
        }

        public Criteria andPartUrlBetween(String value1, String value2) {
            addCriterion("part_url between", value1, value2, "partUrl");
            return (Criteria) this;
        }

        public Criteria andPartUrlNotBetween(String value1, String value2) {
            addCriterion("part_url not between", value1, value2, "partUrl");
            return (Criteria) this;
        }

        public Criteria andPetNameIsNull() {
            addCriterion("pet_name is null");
            return (Criteria) this;
        }

        public Criteria andPetNameIsNotNull() {
            addCriterion("pet_name is not null");
            return (Criteria) this;
        }

        public Criteria andPetNameEqualTo(String value) {
            addCriterion("pet_name =", value, "petName");
            return (Criteria) this;
        }

        public Criteria andPetNameNotEqualTo(String value) {
            addCriterion("pet_name <>", value, "petName");
            return (Criteria) this;
        }

        public Criteria andPetNameGreaterThan(String value) {
            addCriterion("pet_name >", value, "petName");
            return (Criteria) this;
        }

        public Criteria andPetNameGreaterThanOrEqualTo(String value) {
            addCriterion("pet_name >=", value, "petName");
            return (Criteria) this;
        }

        public Criteria andPetNameLessThan(String value) {
            addCriterion("pet_name <", value, "petName");
            return (Criteria) this;
        }

        public Criteria andPetNameLessThanOrEqualTo(String value) {
            addCriterion("pet_name <=", value, "petName");
            return (Criteria) this;
        }

        public Criteria andPetNameLike(String value) {
            addCriterion("pet_name like", value, "petName");
            return (Criteria) this;
        }

        public Criteria andPetNameNotLike(String value) {
            addCriterion("pet_name not like", value, "petName");
            return (Criteria) this;
        }

        public Criteria andPetNameIn(List<String> values) {
            addCriterion("pet_name in", values, "petName");
            return (Criteria) this;
        }

        public Criteria andPetNameNotIn(List<String> values) {
            addCriterion("pet_name not in", values, "petName");
            return (Criteria) this;
        }

        public Criteria andPetNameBetween(String value1, String value2) {
            addCriterion("pet_name between", value1, value2, "petName");
            return (Criteria) this;
        }

        public Criteria andPetNameNotBetween(String value1, String value2) {
            addCriterion("pet_name not between", value1, value2, "petName");
            return (Criteria) this;
        }

        public Criteria andAddressIsNull() {
            addCriterion("address is null");
            return (Criteria) this;
        }

        public Criteria andAddressIsNotNull() {
            addCriterion("address is not null");
            return (Criteria) this;
        }

        public Criteria andAddressEqualTo(String value) {
            addCriterion("address =", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotEqualTo(String value) {
            addCriterion("address <>", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressGreaterThan(String value) {
            addCriterion("address >", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressGreaterThanOrEqualTo(String value) {
            addCriterion("address >=", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressLessThan(String value) {
            addCriterion("address <", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressLessThanOrEqualTo(String value) {
            addCriterion("address <=", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressLike(String value) {
            addCriterion("address like", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotLike(String value) {
            addCriterion("address not like", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressIn(List<String> values) {
            addCriterion("address in", values, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotIn(List<String> values) {
            addCriterion("address not in", values, "address");
            return (Criteria) this;
        }

        public Criteria andAddressBetween(String value1, String value2) {
            addCriterion("address between", value1, value2, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotBetween(String value1, String value2) {
            addCriterion("address not between", value1, value2, "address");
            return (Criteria) this;
        }

        public Criteria andQqIsNull() {
            addCriterion("qq is null");
            return (Criteria) this;
        }

        public Criteria andQqIsNotNull() {
            addCriterion("qq is not null");
            return (Criteria) this;
        }

        public Criteria andQqEqualTo(String value) {
            addCriterion("qq =", value, "qq");
            return (Criteria) this;
        }

        public Criteria andQqNotEqualTo(String value) {
            addCriterion("qq <>", value, "qq");
            return (Criteria) this;
        }

        public Criteria andQqGreaterThan(String value) {
            addCriterion("qq >", value, "qq");
            return (Criteria) this;
        }

        public Criteria andQqGreaterThanOrEqualTo(String value) {
            addCriterion("qq >=", value, "qq");
            return (Criteria) this;
        }

        public Criteria andQqLessThan(String value) {
            addCriterion("qq <", value, "qq");
            return (Criteria) this;
        }

        public Criteria andQqLessThanOrEqualTo(String value) {
            addCriterion("qq <=", value, "qq");
            return (Criteria) this;
        }

        public Criteria andQqLike(String value) {
            addCriterion("qq like", value, "qq");
            return (Criteria) this;
        }

        public Criteria andQqNotLike(String value) {
            addCriterion("qq not like", value, "qq");
            return (Criteria) this;
        }

        public Criteria andQqIn(List<String> values) {
            addCriterion("qq in", values, "qq");
            return (Criteria) this;
        }

        public Criteria andQqNotIn(List<String> values) {
            addCriterion("qq not in", values, "qq");
            return (Criteria) this;
        }

        public Criteria andQqBetween(String value1, String value2) {
            addCriterion("qq between", value1, value2, "qq");
            return (Criteria) this;
        }

        public Criteria andQqNotBetween(String value1, String value2) {
            addCriterion("qq not between", value1, value2, "qq");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolIsNull() {
            addCriterion("practical_school is null");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolIsNotNull() {
            addCriterion("practical_school is not null");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolEqualTo(String value) {
            addCriterion("practical_school =", value, "practicalSchool");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolNotEqualTo(String value) {
            addCriterion("practical_school <>", value, "practicalSchool");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolGreaterThan(String value) {
            addCriterion("practical_school >", value, "practicalSchool");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolGreaterThanOrEqualTo(String value) {
            addCriterion("practical_school >=", value, "practicalSchool");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolLessThan(String value) {
            addCriterion("practical_school <", value, "practicalSchool");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolLessThanOrEqualTo(String value) {
            addCriterion("practical_school <=", value, "practicalSchool");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolLike(String value) {
            addCriterion("practical_school like", value, "practicalSchool");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolNotLike(String value) {
            addCriterion("practical_school not like", value, "practicalSchool");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolIn(List<String> values) {
            addCriterion("practical_school in", values, "practicalSchool");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolNotIn(List<String> values) {
            addCriterion("practical_school not in", values, "practicalSchool");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolBetween(String value1, String value2) {
            addCriterion("practical_school between", value1, value2, "practicalSchool");
            return (Criteria) this;
        }

        public Criteria andPracticalSchoolNotBetween(String value1, String value2) {
            addCriterion("practical_school not between", value1, value2, "practicalSchool");
            return (Criteria) this;
        }

        public Criteria andReferrerIsNull() {
            addCriterion("referrer is null");
            return (Criteria) this;
        }

        public Criteria andReferrerIsNotNull() {
            addCriterion("referrer is not null");
            return (Criteria) this;
        }

        public Criteria andReferrerEqualTo(String value) {
            addCriterion("referrer =", value, "referrer");
            return (Criteria) this;
        }

        public Criteria andReferrerNotEqualTo(String value) {
            addCriterion("referrer <>", value, "referrer");
            return (Criteria) this;
        }

        public Criteria andReferrerGreaterThan(String value) {
            addCriterion("referrer >", value, "referrer");
            return (Criteria) this;
        }

        public Criteria andReferrerGreaterThanOrEqualTo(String value) {
            addCriterion("referrer >=", value, "referrer");
            return (Criteria) this;
        }

        public Criteria andReferrerLessThan(String value) {
            addCriterion("referrer <", value, "referrer");
            return (Criteria) this;
        }

        public Criteria andReferrerLessThanOrEqualTo(String value) {
            addCriterion("referrer <=", value, "referrer");
            return (Criteria) this;
        }

        public Criteria andReferrerLike(String value) {
            addCriterion("referrer like", value, "referrer");
            return (Criteria) this;
        }

        public Criteria andReferrerNotLike(String value) {
            addCriterion("referrer not like", value, "referrer");
            return (Criteria) this;
        }

        public Criteria andReferrerIn(List<String> values) {
            addCriterion("referrer in", values, "referrer");
            return (Criteria) this;
        }

        public Criteria andReferrerNotIn(List<String> values) {
            addCriterion("referrer not in", values, "referrer");
            return (Criteria) this;
        }

        public Criteria andReferrerBetween(String value1, String value2) {
            addCriterion("referrer between", value1, value2, "referrer");
            return (Criteria) this;
        }

        public Criteria andReferrerNotBetween(String value1, String value2) {
            addCriterion("referrer not between", value1, value2, "referrer");
            return (Criteria) this;
        }

        public Criteria andOfflineGoldIsNull() {
            addCriterion("offline_gold is null");
            return (Criteria) this;
        }

        public Criteria andOfflineGoldIsNotNull() {
            addCriterion("offline_gold is not null");
            return (Criteria) this;
        }

        public Criteria andOfflineGoldEqualTo(Double value) {
            addCriterion("offline_gold =", value, "offlineGold");
            return (Criteria) this;
        }

        public Criteria andOfflineGoldNotEqualTo(Double value) {
            addCriterion("offline_gold <>", value, "offlineGold");
            return (Criteria) this;
        }

        public Criteria andOfflineGoldGreaterThan(Double value) {
            addCriterion("offline_gold >", value, "offlineGold");
            return (Criteria) this;
        }

        public Criteria andOfflineGoldGreaterThanOrEqualTo(Double value) {
            addCriterion("offline_gold >=", value, "offlineGold");
            return (Criteria) this;
        }

        public Criteria andOfflineGoldLessThan(Double value) {
            addCriterion("offline_gold <", value, "offlineGold");
            return (Criteria) this;
        }

        public Criteria andOfflineGoldLessThanOrEqualTo(Double value) {
            addCriterion("offline_gold <=", value, "offlineGold");
            return (Criteria) this;
        }

        public Criteria andOfflineGoldIn(List<Double> values) {
            addCriterion("offline_gold in", values, "offlineGold");
            return (Criteria) this;
        }

        public Criteria andOfflineGoldNotIn(List<Double> values) {
            addCriterion("offline_gold not in", values, "offlineGold");
            return (Criteria) this;
        }

        public Criteria andOfflineGoldBetween(Double value1, Double value2) {
            addCriterion("offline_gold between", value1, value2, "offlineGold");
            return (Criteria) this;
        }

        public Criteria andOfflineGoldNotBetween(Double value1, Double value2) {
            addCriterion("offline_gold not between", value1, value2, "offlineGold");
            return (Criteria) this;
        }

        public Criteria andSystemGoldIsNull() {
            addCriterion("system_gold is null");
            return (Criteria) this;
        }

        public Criteria andSystemGoldIsNotNull() {
            addCriterion("system_gold is not null");
            return (Criteria) this;
        }

        public Criteria andSystemGoldEqualTo(Double value) {
            addCriterion("system_gold =", value, "systemGold");
            return (Criteria) this;
        }

        public Criteria andSystemGoldNotEqualTo(Double value) {
            addCriterion("system_gold <>", value, "systemGold");
            return (Criteria) this;
        }

        public Criteria andSystemGoldGreaterThan(Double value) {
            addCriterion("system_gold >", value, "systemGold");
            return (Criteria) this;
        }

        public Criteria andSystemGoldGreaterThanOrEqualTo(Double value) {
            addCriterion("system_gold >=", value, "systemGold");
            return (Criteria) this;
        }

        public Criteria andSystemGoldLessThan(Double value) {
            addCriterion("system_gold <", value, "systemGold");
            return (Criteria) this;
        }

        public Criteria andSystemGoldLessThanOrEqualTo(Double value) {
            addCriterion("system_gold <=", value, "systemGold");
            return (Criteria) this;
        }

        public Criteria andSystemGoldIn(List<Double> values) {
            addCriterion("system_gold in", values, "systemGold");
            return (Criteria) this;
        }

        public Criteria andSystemGoldNotIn(List<Double> values) {
            addCriterion("system_gold not in", values, "systemGold");
            return (Criteria) this;
        }

        public Criteria andSystemGoldBetween(Double value1, Double value2) {
            addCriterion("system_gold between", value1, value2, "systemGold");
            return (Criteria) this;
        }

        public Criteria andSystemGoldNotBetween(Double value1, Double value2) {
            addCriterion("system_gold not between", value1, value2, "systemGold");
            return (Criteria) this;
        }

        public Criteria andSchoolGoldFirstTimeIsNull() {
            addCriterion("school_gold_first_time is null");
            return (Criteria) this;
        }

        public Criteria andSchoolGoldFirstTimeIsNotNull() {
            addCriterion("school_gold_first_time is not null");
            return (Criteria) this;
        }

        public Criteria andSchoolGoldFirstTimeEqualTo(Date value) {
            addCriterion("school_gold_first_time =", value, "schoolGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andSchoolGoldFirstTimeNotEqualTo(Date value) {
            addCriterion("school_gold_first_time <>", value, "schoolGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andSchoolGoldFirstTimeGreaterThan(Date value) {
            addCriterion("school_gold_first_time >", value, "schoolGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andSchoolGoldFirstTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("school_gold_first_time >=", value, "schoolGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andSchoolGoldFirstTimeLessThan(Date value) {
            addCriterion("school_gold_first_time <", value, "schoolGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andSchoolGoldFirstTimeLessThanOrEqualTo(Date value) {
            addCriterion("school_gold_first_time <=", value, "schoolGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andSchoolGoldFirstTimeIn(List<Date> values) {
            addCriterion("school_gold_first_time in", values, "schoolGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andSchoolGoldFirstTimeNotIn(List<Date> values) {
            addCriterion("school_gold_first_time not in", values, "schoolGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andSchoolGoldFirstTimeBetween(Date value1, Date value2) {
            addCriterion("school_gold_first_time between", value1, value2, "schoolGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andSchoolGoldFirstTimeNotBetween(Date value1, Date value2) {
            addCriterion("school_gold_first_time not between", value1, value2, "schoolGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andCountryGoldFirstTimeIsNull() {
            addCriterion("country_gold_first_time is null");
            return (Criteria) this;
        }

        public Criteria andCountryGoldFirstTimeIsNotNull() {
            addCriterion("country_gold_first_time is not null");
            return (Criteria) this;
        }

        public Criteria andCountryGoldFirstTimeEqualTo(Date value) {
            addCriterion("country_gold_first_time =", value, "countryGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andCountryGoldFirstTimeNotEqualTo(Date value) {
            addCriterion("country_gold_first_time <>", value, "countryGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andCountryGoldFirstTimeGreaterThan(Date value) {
            addCriterion("country_gold_first_time >", value, "countryGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andCountryGoldFirstTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("country_gold_first_time >=", value, "countryGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andCountryGoldFirstTimeLessThan(Date value) {
            addCriterion("country_gold_first_time <", value, "countryGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andCountryGoldFirstTimeLessThanOrEqualTo(Date value) {
            addCriterion("country_gold_first_time <=", value, "countryGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andCountryGoldFirstTimeIn(List<Date> values) {
            addCriterion("country_gold_first_time in", values, "countryGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andCountryGoldFirstTimeNotIn(List<Date> values) {
            addCriterion("country_gold_first_time not in", values, "countryGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andCountryGoldFirstTimeBetween(Date value1, Date value2) {
            addCriterion("country_gold_first_time between", value1, value2, "countryGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andCountryGoldFirstTimeNotBetween(Date value1, Date value2) {
            addCriterion("country_gold_first_time not between", value1, value2, "countryGoldFirstTime");
            return (Criteria) this;
        }

        public Criteria andWorshipFirstTimeIsNull() {
            addCriterion("worship_first_time is null");
            return (Criteria) this;
        }

        public Criteria andWorshipFirstTimeIsNotNull() {
            addCriterion("worship_first_time is not null");
            return (Criteria) this;
        }

        public Criteria andWorshipFirstTimeEqualTo(Date value) {
            addCriterion("worship_first_time =", value, "worshipFirstTime");
            return (Criteria) this;
        }

        public Criteria andWorshipFirstTimeNotEqualTo(Date value) {
            addCriterion("worship_first_time <>", value, "worshipFirstTime");
            return (Criteria) this;
        }

        public Criteria andWorshipFirstTimeGreaterThan(Date value) {
            addCriterion("worship_first_time >", value, "worshipFirstTime");
            return (Criteria) this;
        }

        public Criteria andWorshipFirstTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("worship_first_time >=", value, "worshipFirstTime");
            return (Criteria) this;
        }

        public Criteria andWorshipFirstTimeLessThan(Date value) {
            addCriterion("worship_first_time <", value, "worshipFirstTime");
            return (Criteria) this;
        }

        public Criteria andWorshipFirstTimeLessThanOrEqualTo(Date value) {
            addCriterion("worship_first_time <=", value, "worshipFirstTime");
            return (Criteria) this;
        }

        public Criteria andWorshipFirstTimeIn(List<Date> values) {
            addCriterion("worship_first_time in", values, "worshipFirstTime");
            return (Criteria) this;
        }

        public Criteria andWorshipFirstTimeNotIn(List<Date> values) {
            addCriterion("worship_first_time not in", values, "worshipFirstTime");
            return (Criteria) this;
        }

        public Criteria andWorshipFirstTimeBetween(Date value1, Date value2) {
            addCriterion("worship_first_time between", value1, value2, "worshipFirstTime");
            return (Criteria) this;
        }

        public Criteria andWorshipFirstTimeNotBetween(Date value1, Date value2) {
            addCriterion("worship_first_time not between", value1, value2, "worshipFirstTime");
            return (Criteria) this;
        }

        public Criteria andRankIsNull() {
            addCriterion("rank is null");
            return (Criteria) this;
        }

        public Criteria andRankIsNotNull() {
            addCriterion("rank is not null");
            return (Criteria) this;
        }

        public Criteria andRankEqualTo(Integer value) {
            addCriterion("rank =", value, "rank");
            return (Criteria) this;
        }

        public Criteria andRankNotEqualTo(Integer value) {
            addCriterion("rank <>", value, "rank");
            return (Criteria) this;
        }

        public Criteria andRankGreaterThan(Integer value) {
            addCriterion("rank >", value, "rank");
            return (Criteria) this;
        }

        public Criteria andRankGreaterThanOrEqualTo(Integer value) {
            addCriterion("rank >=", value, "rank");
            return (Criteria) this;
        }

        public Criteria andRankLessThan(Integer value) {
            addCriterion("rank <", value, "rank");
            return (Criteria) this;
        }

        public Criteria andRankLessThanOrEqualTo(Integer value) {
            addCriterion("rank <=", value, "rank");
            return (Criteria) this;
        }

        public Criteria andRankIn(List<Integer> values) {
            addCriterion("rank in", values, "rank");
            return (Criteria) this;
        }

        public Criteria andRankNotIn(List<Integer> values) {
            addCriterion("rank not in", values, "rank");
            return (Criteria) this;
        }

        public Criteria andRankBetween(Integer value1, Integer value2) {
            addCriterion("rank between", value1, value2, "rank");
            return (Criteria) this;
        }

        public Criteria andRankNotBetween(Integer value1, Integer value2) {
            addCriterion("rank not between", value1, value2, "rank");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andRoleIsNull() {
            addCriterion("role is null");
            return (Criteria) this;
        }

        public Criteria andRoleIsNotNull() {
            addCriterion("role is not null");
            return (Criteria) this;
        }

        public Criteria andRoleEqualTo(Integer value) {
            addCriterion("role =", value, "role");
            return (Criteria) this;
        }

        public Criteria andRoleNotEqualTo(Integer value) {
            addCriterion("role <>", value, "role");
            return (Criteria) this;
        }

        public Criteria andRoleGreaterThan(Integer value) {
            addCriterion("role >", value, "role");
            return (Criteria) this;
        }

        public Criteria andRoleGreaterThanOrEqualTo(Integer value) {
            addCriterion("role >=", value, "role");
            return (Criteria) this;
        }

        public Criteria andRoleLessThan(Integer value) {
            addCriterion("role <", value, "role");
            return (Criteria) this;
        }

        public Criteria andRoleLessThanOrEqualTo(Integer value) {
            addCriterion("role <=", value, "role");
            return (Criteria) this;
        }

        public Criteria andRoleIn(List<Integer> values) {
            addCriterion("role in", values, "role");
            return (Criteria) this;
        }

        public Criteria andRoleNotIn(List<Integer> values) {
            addCriterion("role not in", values, "role");
            return (Criteria) this;
        }

        public Criteria andRoleBetween(Integer value1, Integer value2) {
            addCriterion("role between", value1, value2, "role");
            return (Criteria) this;
        }

        public Criteria andRoleNotBetween(Integer value1, Integer value2) {
            addCriterion("role not between", value1, value2, "role");
            return (Criteria) this;
        }

        public Criteria andVersionIsNull() {
            addCriterion("version is null");
            return (Criteria) this;
        }

        public Criteria andVersionIsNotNull() {
            addCriterion("version is not null");
            return (Criteria) this;
        }

        public Criteria andVersionEqualTo(String value) {
            addCriterion("version =", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotEqualTo(String value) {
            addCriterion("version <>", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThan(String value) {
            addCriterion("version >", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThanOrEqualTo(String value) {
            addCriterion("version >=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThan(String value) {
            addCriterion("version <", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThanOrEqualTo(String value) {
            addCriterion("version <=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLike(String value) {
            addCriterion("version like", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotLike(String value) {
            addCriterion("version not like", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionIn(List<String> values) {
            addCriterion("version in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotIn(List<String> values) {
            addCriterion("version not in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionBetween(String value1, String value2) {
            addCriterion("version between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotBetween(String value1, String value2) {
            addCriterion("version not between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeIsNull() {
            addCriterion("first_study_time is null");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeIsNotNull() {
            addCriterion("first_study_time is not null");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeEqualTo(Date value) {
            addCriterion("first_study_time =", value, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeNotEqualTo(Date value) {
            addCriterion("first_study_time <>", value, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeGreaterThan(Date value) {
            addCriterion("first_study_time >", value, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("first_study_time >=", value, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeLessThan(Date value) {
            addCriterion("first_study_time <", value, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeLessThanOrEqualTo(Date value) {
            addCriterion("first_study_time <=", value, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeIn(List<Date> values) {
            addCriterion("first_study_time in", values, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeNotIn(List<Date> values) {
            addCriterion("first_study_time not in", values, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeBetween(Date value1, Date value2) {
            addCriterion("first_study_time between", value1, value2, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andFirstStudyTimeNotBetween(Date value1, Date value2) {
            addCriterion("first_study_time not between", value1, value2, "firstStudyTime");
            return (Criteria) this;
        }

        public Criteria andCourseIdIsNull() {
            addCriterion("course_id is null");
            return (Criteria) this;
        }

        public Criteria andCourseIdIsNotNull() {
            addCriterion("course_id is not null");
            return (Criteria) this;
        }

        public Criteria andCourseIdEqualTo(Long value) {
            addCriterion("course_id =", value, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdNotEqualTo(Long value) {
            addCriterion("course_id <>", value, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdGreaterThan(Long value) {
            addCriterion("course_id >", value, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdGreaterThanOrEqualTo(Long value) {
            addCriterion("course_id >=", value, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdLessThan(Long value) {
            addCriterion("course_id <", value, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdLessThanOrEqualTo(Long value) {
            addCriterion("course_id <=", value, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdIn(List<Long> values) {
            addCriterion("course_id in", values, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdNotIn(List<Long> values) {
            addCriterion("course_id not in", values, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdBetween(Long value1, Long value2) {
            addCriterion("course_id between", value1, value2, "courseId");
            return (Criteria) this;
        }

        public Criteria andCourseIdNotBetween(Long value1, Long value2) {
            addCriterion("course_id not between", value1, value2, "courseId");
            return (Criteria) this;
        }

        public Criteria andUnitIdIsNull() {
            addCriterion("unit_id is null");
            return (Criteria) this;
        }

        public Criteria andUnitIdIsNotNull() {
            addCriterion("unit_id is not null");
            return (Criteria) this;
        }

        public Criteria andUnitIdEqualTo(Long value) {
            addCriterion("unit_id =", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdNotEqualTo(Long value) {
            addCriterion("unit_id <>", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdGreaterThan(Long value) {
            addCriterion("unit_id >", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdGreaterThanOrEqualTo(Long value) {
            addCriterion("unit_id >=", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdLessThan(Long value) {
            addCriterion("unit_id <", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdLessThanOrEqualTo(Long value) {
            addCriterion("unit_id <=", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdIn(List<Long> values) {
            addCriterion("unit_id in", values, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdNotIn(List<Long> values) {
            addCriterion("unit_id not in", values, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdBetween(Long value1, Long value2) {
            addCriterion("unit_id between", value1, value2, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdNotBetween(Long value1, Long value2) {
            addCriterion("unit_id not between", value1, value2, "unitId");
            return (Criteria) this;
        }

        public Criteria andCourseNameIsNull() {
            addCriterion("course_name is null");
            return (Criteria) this;
        }

        public Criteria andCourseNameIsNotNull() {
            addCriterion("course_name is not null");
            return (Criteria) this;
        }

        public Criteria andCourseNameEqualTo(String value) {
            addCriterion("course_name =", value, "courseName");
            return (Criteria) this;
        }

        public Criteria andCourseNameNotEqualTo(String value) {
            addCriterion("course_name <>", value, "courseName");
            return (Criteria) this;
        }

        public Criteria andCourseNameGreaterThan(String value) {
            addCriterion("course_name >", value, "courseName");
            return (Criteria) this;
        }

        public Criteria andCourseNameGreaterThanOrEqualTo(String value) {
            addCriterion("course_name >=", value, "courseName");
            return (Criteria) this;
        }

        public Criteria andCourseNameLessThan(String value) {
            addCriterion("course_name <", value, "courseName");
            return (Criteria) this;
        }

        public Criteria andCourseNameLessThanOrEqualTo(String value) {
            addCriterion("course_name <=", value, "courseName");
            return (Criteria) this;
        }

        public Criteria andCourseNameLike(String value) {
            addCriterion("course_name like", value, "courseName");
            return (Criteria) this;
        }

        public Criteria andCourseNameNotLike(String value) {
            addCriterion("course_name not like", value, "courseName");
            return (Criteria) this;
        }

        public Criteria andCourseNameIn(List<String> values) {
            addCriterion("course_name in", values, "courseName");
            return (Criteria) this;
        }

        public Criteria andCourseNameNotIn(List<String> values) {
            addCriterion("course_name not in", values, "courseName");
            return (Criteria) this;
        }

        public Criteria andCourseNameBetween(String value1, String value2) {
            addCriterion("course_name between", value1, value2, "courseName");
            return (Criteria) this;
        }

        public Criteria andCourseNameNotBetween(String value1, String value2) {
            addCriterion("course_name not between", value1, value2, "courseName");
            return (Criteria) this;
        }

        public Criteria andUnitNameIsNull() {
            addCriterion("unit_name is null");
            return (Criteria) this;
        }

        public Criteria andUnitNameIsNotNull() {
            addCriterion("unit_name is not null");
            return (Criteria) this;
        }

        public Criteria andUnitNameEqualTo(String value) {
            addCriterion("unit_name =", value, "unitName");
            return (Criteria) this;
        }

        public Criteria andUnitNameNotEqualTo(String value) {
            addCriterion("unit_name <>", value, "unitName");
            return (Criteria) this;
        }

        public Criteria andUnitNameGreaterThan(String value) {
            addCriterion("unit_name >", value, "unitName");
            return (Criteria) this;
        }

        public Criteria andUnitNameGreaterThanOrEqualTo(String value) {
            addCriterion("unit_name >=", value, "unitName");
            return (Criteria) this;
        }

        public Criteria andUnitNameLessThan(String value) {
            addCriterion("unit_name <", value, "unitName");
            return (Criteria) this;
        }

        public Criteria andUnitNameLessThanOrEqualTo(String value) {
            addCriterion("unit_name <=", value, "unitName");
            return (Criteria) this;
        }

        public Criteria andUnitNameLike(String value) {
            addCriterion("unit_name like", value, "unitName");
            return (Criteria) this;
        }

        public Criteria andUnitNameNotLike(String value) {
            addCriterion("unit_name not like", value, "unitName");
            return (Criteria) this;
        }

        public Criteria andUnitNameIn(List<String> values) {
            addCriterion("unit_name in", values, "unitName");
            return (Criteria) this;
        }

        public Criteria andUnitNameNotIn(List<String> values) {
            addCriterion("unit_name not in", values, "unitName");
            return (Criteria) this;
        }

        public Criteria andUnitNameBetween(String value1, String value2) {
            addCriterion("unit_name between", value1, value2, "unitName");
            return (Criteria) this;
        }

        public Criteria andUnitNameNotBetween(String value1, String value2) {
            addCriterion("unit_name not between", value1, value2, "unitName");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseIdIsNull() {
            addCriterion("sentence_course_id is null");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseIdIsNotNull() {
            addCriterion("sentence_course_id is not null");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseIdEqualTo(Long value) {
            addCriterion("sentence_course_id =", value, "sentenceCourseId");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseIdNotEqualTo(Long value) {
            addCriterion("sentence_course_id <>", value, "sentenceCourseId");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseIdGreaterThan(Long value) {
            addCriterion("sentence_course_id >", value, "sentenceCourseId");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseIdGreaterThanOrEqualTo(Long value) {
            addCriterion("sentence_course_id >=", value, "sentenceCourseId");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseIdLessThan(Long value) {
            addCriterion("sentence_course_id <", value, "sentenceCourseId");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseIdLessThanOrEqualTo(Long value) {
            addCriterion("sentence_course_id <=", value, "sentenceCourseId");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseIdIn(List<Long> values) {
            addCriterion("sentence_course_id in", values, "sentenceCourseId");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseIdNotIn(List<Long> values) {
            addCriterion("sentence_course_id not in", values, "sentenceCourseId");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseIdBetween(Long value1, Long value2) {
            addCriterion("sentence_course_id between", value1, value2, "sentenceCourseId");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseIdNotBetween(Long value1, Long value2) {
            addCriterion("sentence_course_id not between", value1, value2, "sentenceCourseId");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitIdIsNull() {
            addCriterion("sentence_unit_id is null");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitIdIsNotNull() {
            addCriterion("sentence_unit_id is not null");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitIdEqualTo(Integer value) {
            addCriterion("sentence_unit_id =", value, "sentenceUnitId");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitIdNotEqualTo(Integer value) {
            addCriterion("sentence_unit_id <>", value, "sentenceUnitId");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitIdGreaterThan(Integer value) {
            addCriterion("sentence_unit_id >", value, "sentenceUnitId");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("sentence_unit_id >=", value, "sentenceUnitId");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitIdLessThan(Integer value) {
            addCriterion("sentence_unit_id <", value, "sentenceUnitId");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitIdLessThanOrEqualTo(Integer value) {
            addCriterion("sentence_unit_id <=", value, "sentenceUnitId");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitIdIn(List<Integer> values) {
            addCriterion("sentence_unit_id in", values, "sentenceUnitId");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitIdNotIn(List<Integer> values) {
            addCriterion("sentence_unit_id not in", values, "sentenceUnitId");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitIdBetween(Integer value1, Integer value2) {
            addCriterion("sentence_unit_id between", value1, value2, "sentenceUnitId");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitIdNotBetween(Integer value1, Integer value2) {
            addCriterion("sentence_unit_id not between", value1, value2, "sentenceUnitId");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameIsNull() {
            addCriterion("sentence_course_name is null");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameIsNotNull() {
            addCriterion("sentence_course_name is not null");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameEqualTo(String value) {
            addCriterion("sentence_course_name =", value, "sentenceCourseName");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameNotEqualTo(String value) {
            addCriterion("sentence_course_name <>", value, "sentenceCourseName");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameGreaterThan(String value) {
            addCriterion("sentence_course_name >", value, "sentenceCourseName");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameGreaterThanOrEqualTo(String value) {
            addCriterion("sentence_course_name >=", value, "sentenceCourseName");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameLessThan(String value) {
            addCriterion("sentence_course_name <", value, "sentenceCourseName");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameLessThanOrEqualTo(String value) {
            addCriterion("sentence_course_name <=", value, "sentenceCourseName");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameLike(String value) {
            addCriterion("sentence_course_name like", value, "sentenceCourseName");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameNotLike(String value) {
            addCriterion("sentence_course_name not like", value, "sentenceCourseName");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameIn(List<String> values) {
            addCriterion("sentence_course_name in", values, "sentenceCourseName");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameNotIn(List<String> values) {
            addCriterion("sentence_course_name not in", values, "sentenceCourseName");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameBetween(String value1, String value2) {
            addCriterion("sentence_course_name between", value1, value2, "sentenceCourseName");
            return (Criteria) this;
        }

        public Criteria andSentenceCourseNameNotBetween(String value1, String value2) {
            addCriterion("sentence_course_name not between", value1, value2, "sentenceCourseName");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameIsNull() {
            addCriterion("sentence_unit_name is null");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameIsNotNull() {
            addCriterion("sentence_unit_name is not null");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameEqualTo(String value) {
            addCriterion("sentence_unit_name =", value, "sentenceUnitName");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameNotEqualTo(String value) {
            addCriterion("sentence_unit_name <>", value, "sentenceUnitName");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameGreaterThan(String value) {
            addCriterion("sentence_unit_name >", value, "sentenceUnitName");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameGreaterThanOrEqualTo(String value) {
            addCriterion("sentence_unit_name >=", value, "sentenceUnitName");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameLessThan(String value) {
            addCriterion("sentence_unit_name <", value, "sentenceUnitName");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameLessThanOrEqualTo(String value) {
            addCriterion("sentence_unit_name <=", value, "sentenceUnitName");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameLike(String value) {
            addCriterion("sentence_unit_name like", value, "sentenceUnitName");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameNotLike(String value) {
            addCriterion("sentence_unit_name not like", value, "sentenceUnitName");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameIn(List<String> values) {
            addCriterion("sentence_unit_name in", values, "sentenceUnitName");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameNotIn(List<String> values) {
            addCriterion("sentence_unit_name not in", values, "sentenceUnitName");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameBetween(String value1, String value2) {
            addCriterion("sentence_unit_name between", value1, value2, "sentenceUnitName");
            return (Criteria) this;
        }

        public Criteria andSentenceUnitNameNotBetween(String value1, String value2) {
            addCriterion("sentence_unit_name not between", value1, value2, "sentenceUnitName");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}