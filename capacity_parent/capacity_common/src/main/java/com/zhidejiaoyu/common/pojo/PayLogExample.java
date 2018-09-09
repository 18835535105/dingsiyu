package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PayLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PayLogExample() {
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

        public Criteria andStudentIdIsNull() {
            addCriterion("student_id is null");
            return (Criteria) this;
        }

        public Criteria andStudentIdIsNotNull() {
            addCriterion("student_id is not null");
            return (Criteria) this;
        }

        public Criteria andStudentIdEqualTo(Long value) {
            addCriterion("student_id =", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotEqualTo(Long value) {
            addCriterion("student_id <>", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdGreaterThan(Long value) {
            addCriterion("student_id >", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("student_id >=", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdLessThan(Long value) {
            addCriterion("student_id <", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdLessThanOrEqualTo(Long value) {
            addCriterion("student_id <=", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdIn(List<Long> values) {
            addCriterion("student_id in", values, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotIn(List<Long> values) {
            addCriterion("student_id not in", values, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdBetween(Long value1, Long value2) {
            addCriterion("student_id between", value1, value2, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotBetween(Long value1, Long value2) {
            addCriterion("student_id not between", value1, value2, "studentId");
            return (Criteria) this;
        }

        public Criteria andRechargeIsNull() {
            addCriterion("recharge is null");
            return (Criteria) this;
        }

        public Criteria andRechargeIsNotNull() {
            addCriterion("recharge is not null");
            return (Criteria) this;
        }

        public Criteria andRechargeEqualTo(Date value) {
            addCriterionForJDBCDate("recharge =", value, "recharge");
            return (Criteria) this;
        }

        public Criteria andRechargeNotEqualTo(Date value) {
            addCriterionForJDBCDate("recharge <>", value, "recharge");
            return (Criteria) this;
        }

        public Criteria andRechargeGreaterThan(Date value) {
            addCriterionForJDBCDate("recharge >", value, "recharge");
            return (Criteria) this;
        }

        public Criteria andRechargeGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("recharge >=", value, "recharge");
            return (Criteria) this;
        }

        public Criteria andRechargeLessThan(Date value) {
            addCriterionForJDBCDate("recharge <", value, "recharge");
            return (Criteria) this;
        }

        public Criteria andRechargeLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("recharge <=", value, "recharge");
            return (Criteria) this;
        }

        public Criteria andRechargeIn(List<Date> values) {
            addCriterionForJDBCDate("recharge in", values, "recharge");
            return (Criteria) this;
        }

        public Criteria andRechargeNotIn(List<Date> values) {
            addCriterionForJDBCDate("recharge not in", values, "recharge");
            return (Criteria) this;
        }

        public Criteria andRechargeBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("recharge between", value1, value2, "recharge");
            return (Criteria) this;
        }

        public Criteria andRechargeNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("recharge not between", value1, value2, "recharge");
            return (Criteria) this;
        }

        public Criteria andCardNoIsNull() {
            addCriterion("card_no is null");
            return (Criteria) this;
        }

        public Criteria andCardNoIsNotNull() {
            addCriterion("card_no is not null");
            return (Criteria) this;
        }

        public Criteria andCardNoEqualTo(String value) {
            addCriterion("card_no =", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotEqualTo(String value) {
            addCriterion("card_no <>", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoGreaterThan(String value) {
            addCriterion("card_no >", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoGreaterThanOrEqualTo(String value) {
            addCriterion("card_no >=", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoLessThan(String value) {
            addCriterion("card_no <", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoLessThanOrEqualTo(String value) {
            addCriterion("card_no <=", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoLike(String value) {
            addCriterion("card_no like", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotLike(String value) {
            addCriterion("card_no not like", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoIn(List<String> values) {
            addCriterion("card_no in", values, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotIn(List<String> values) {
            addCriterion("card_no not in", values, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoBetween(String value1, String value2) {
            addCriterion("card_no between", value1, value2, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotBetween(String value1, String value2) {
            addCriterion("card_no not between", value1, value2, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardDateIsNull() {
            addCriterion("card_date is null");
            return (Criteria) this;
        }

        public Criteria andCardDateIsNotNull() {
            addCriterion("card_date is not null");
            return (Criteria) this;
        }

        public Criteria andCardDateEqualTo(Integer value) {
            addCriterion("card_date =", value, "cardDate");
            return (Criteria) this;
        }

        public Criteria andCardDateNotEqualTo(Integer value) {
            addCriterion("card_date <>", value, "cardDate");
            return (Criteria) this;
        }

        public Criteria andCardDateGreaterThan(Integer value) {
            addCriterion("card_date >", value, "cardDate");
            return (Criteria) this;
        }

        public Criteria andCardDateGreaterThanOrEqualTo(Integer value) {
            addCriterion("card_date >=", value, "cardDate");
            return (Criteria) this;
        }

        public Criteria andCardDateLessThan(Integer value) {
            addCriterion("card_date <", value, "cardDate");
            return (Criteria) this;
        }

        public Criteria andCardDateLessThanOrEqualTo(Integer value) {
            addCriterion("card_date <=", value, "cardDate");
            return (Criteria) this;
        }

        public Criteria andCardDateIn(List<Integer> values) {
            addCriterion("card_date in", values, "cardDate");
            return (Criteria) this;
        }

        public Criteria andCardDateNotIn(List<Integer> values) {
            addCriterion("card_date not in", values, "cardDate");
            return (Criteria) this;
        }

        public Criteria andCardDateBetween(Integer value1, Integer value2) {
            addCriterion("card_date between", value1, value2, "cardDate");
            return (Criteria) this;
        }

        public Criteria andCardDateNotBetween(Integer value1, Integer value2) {
            addCriterion("card_date not between", value1, value2, "cardDate");
            return (Criteria) this;
        }

        public Criteria andFoundDateIsNull() {
            addCriterion("found_date is null");
            return (Criteria) this;
        }

        public Criteria andFoundDateIsNotNull() {
            addCriterion("found_date is not null");
            return (Criteria) this;
        }

        public Criteria andFoundDateEqualTo(Date value) {
            addCriterion("found_date =", value, "foundDate");
            return (Criteria) this;
        }

        public Criteria andFoundDateNotEqualTo(Date value) {
            addCriterion("found_date <>", value, "foundDate");
            return (Criteria) this;
        }

        public Criteria andFoundDateGreaterThan(Date value) {
            addCriterion("found_date >", value, "foundDate");
            return (Criteria) this;
        }

        public Criteria andFoundDateGreaterThanOrEqualTo(Date value) {
            addCriterion("found_date >=", value, "foundDate");
            return (Criteria) this;
        }

        public Criteria andFoundDateLessThan(Date value) {
            addCriterion("found_date <", value, "foundDate");
            return (Criteria) this;
        }

        public Criteria andFoundDateLessThanOrEqualTo(Date value) {
            addCriterion("found_date <=", value, "foundDate");
            return (Criteria) this;
        }

        public Criteria andFoundDateIn(List<Date> values) {
            addCriterion("found_date in", values, "foundDate");
            return (Criteria) this;
        }

        public Criteria andFoundDateNotIn(List<Date> values) {
            addCriterion("found_date not in", values, "foundDate");
            return (Criteria) this;
        }

        public Criteria andFoundDateBetween(Date value1, Date value2) {
            addCriterion("found_date between", value1, value2, "foundDate");
            return (Criteria) this;
        }

        public Criteria andFoundDateNotBetween(Date value1, Date value2) {
            addCriterion("found_date not between", value1, value2, "foundDate");
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