package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CcieExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public CcieExample() {
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

        public Criteria andGetTimeIsNull() {
            addCriterion("get_time is null");
            return (Criteria) this;
        }

        public Criteria andGetTimeIsNotNull() {
            addCriterion("get_time is not null");
            return (Criteria) this;
        }

        public Criteria andGetTimeEqualTo(Date value) {
            addCriterion("get_time =", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeNotEqualTo(Date value) {
            addCriterion("get_time <>", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeGreaterThan(Date value) {
            addCriterion("get_time >", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("get_time >=", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeLessThan(Date value) {
            addCriterion("get_time <", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeLessThanOrEqualTo(Date value) {
            addCriterion("get_time <=", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeIn(List<Date> values) {
            addCriterion("get_time in", values, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeNotIn(List<Date> values) {
            addCriterion("get_time not in", values, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeBetween(Date value1, Date value2) {
            addCriterion("get_time between", value1, value2, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeNotBetween(Date value1, Date value2) {
            addCriterion("get_time not between", value1, value2, "getTime");
            return (Criteria) this;
        }

        public Criteria andTestTypeIsNull() {
            addCriterion("test_type is null");
            return (Criteria) this;
        }

        public Criteria andTestTypeIsNotNull() {
            addCriterion("test_type is not null");
            return (Criteria) this;
        }

        public Criteria andTestTypeEqualTo(Integer value) {
            addCriterion("test_type =", value, "testType");
            return (Criteria) this;
        }

        public Criteria andTestTypeNotEqualTo(Integer value) {
            addCriterion("test_type <>", value, "testType");
            return (Criteria) this;
        }

        public Criteria andTestTypeGreaterThan(Integer value) {
            addCriterion("test_type >", value, "testType");
            return (Criteria) this;
        }

        public Criteria andTestTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("test_type >=", value, "testType");
            return (Criteria) this;
        }

        public Criteria andTestTypeLessThan(Integer value) {
            addCriterion("test_type <", value, "testType");
            return (Criteria) this;
        }

        public Criteria andTestTypeLessThanOrEqualTo(Integer value) {
            addCriterion("test_type <=", value, "testType");
            return (Criteria) this;
        }

        public Criteria andTestTypeIn(List<Integer> values) {
            addCriterion("test_type in", values, "testType");
            return (Criteria) this;
        }

        public Criteria andTestTypeNotIn(List<Integer> values) {
            addCriterion("test_type not in", values, "testType");
            return (Criteria) this;
        }

        public Criteria andTestTypeBetween(Integer value1, Integer value2) {
            addCriterion("test_type between", value1, value2, "testType");
            return (Criteria) this;
        }

        public Criteria andTestTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("test_type not between", value1, value2, "testType");
            return (Criteria) this;
        }

        public Criteria andStudyModelIsNull() {
            addCriterion("study_model is null");
            return (Criteria) this;
        }

        public Criteria andStudyModelIsNotNull() {
            addCriterion("study_model is not null");
            return (Criteria) this;
        }

        public Criteria andStudyModelEqualTo(Integer value) {
            addCriterion("study_model =", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelNotEqualTo(Integer value) {
            addCriterion("study_model <>", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelGreaterThan(Integer value) {
            addCriterion("study_model >", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelGreaterThanOrEqualTo(Integer value) {
            addCriterion("study_model >=", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelLessThan(Integer value) {
            addCriterion("study_model <", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelLessThanOrEqualTo(Integer value) {
            addCriterion("study_model <=", value, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelIn(List<Integer> values) {
            addCriterion("study_model in", values, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelNotIn(List<Integer> values) {
            addCriterion("study_model not in", values, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelBetween(Integer value1, Integer value2) {
            addCriterion("study_model between", value1, value2, "studyModel");
            return (Criteria) this;
        }

        public Criteria andStudyModelNotBetween(Integer value1, Integer value2) {
            addCriterion("study_model not between", value1, value2, "studyModel");
            return (Criteria) this;
        }

        public Criteria andCcieNoIsNull() {
            addCriterion("ccie_no is null");
            return (Criteria) this;
        }

        public Criteria andCcieNoIsNotNull() {
            addCriterion("ccie_no is not null");
            return (Criteria) this;
        }

        public Criteria andCcieNoEqualTo(String value) {
            addCriterion("ccie_no =", value, "ccieNo");
            return (Criteria) this;
        }

        public Criteria andCcieNoNotEqualTo(String value) {
            addCriterion("ccie_no <>", value, "ccieNo");
            return (Criteria) this;
        }

        public Criteria andCcieNoGreaterThan(String value) {
            addCriterion("ccie_no >", value, "ccieNo");
            return (Criteria) this;
        }

        public Criteria andCcieNoGreaterThanOrEqualTo(String value) {
            addCriterion("ccie_no >=", value, "ccieNo");
            return (Criteria) this;
        }

        public Criteria andCcieNoLessThan(String value) {
            addCriterion("ccie_no <", value, "ccieNo");
            return (Criteria) this;
        }

        public Criteria andCcieNoLessThanOrEqualTo(String value) {
            addCriterion("ccie_no <=", value, "ccieNo");
            return (Criteria) this;
        }

        public Criteria andCcieNoLike(String value) {
            addCriterion("ccie_no like", value, "ccieNo");
            return (Criteria) this;
        }

        public Criteria andCcieNoNotLike(String value) {
            addCriterion("ccie_no not like", value, "ccieNo");
            return (Criteria) this;
        }

        public Criteria andCcieNoIn(List<String> values) {
            addCriterion("ccie_no in", values, "ccieNo");
            return (Criteria) this;
        }

        public Criteria andCcieNoNotIn(List<String> values) {
            addCriterion("ccie_no not in", values, "ccieNo");
            return (Criteria) this;
        }

        public Criteria andCcieNoBetween(String value1, String value2) {
            addCriterion("ccie_no between", value1, value2, "ccieNo");
            return (Criteria) this;
        }

        public Criteria andCcieNoNotBetween(String value1, String value2) {
            addCriterion("ccie_no not between", value1, value2, "ccieNo");
            return (Criteria) this;
        }

        public Criteria andEncourageWordIsNull() {
            addCriterion("encourage_word is null");
            return (Criteria) this;
        }

        public Criteria andEncourageWordIsNotNull() {
            addCriterion("encourage_word is not null");
            return (Criteria) this;
        }

        public Criteria andEncourageWordEqualTo(String value) {
            addCriterion("encourage_word =", value, "encourageWord");
            return (Criteria) this;
        }

        public Criteria andEncourageWordNotEqualTo(String value) {
            addCriterion("encourage_word <>", value, "encourageWord");
            return (Criteria) this;
        }

        public Criteria andEncourageWordGreaterThan(String value) {
            addCriterion("encourage_word >", value, "encourageWord");
            return (Criteria) this;
        }

        public Criteria andEncourageWordGreaterThanOrEqualTo(String value) {
            addCriterion("encourage_word >=", value, "encourageWord");
            return (Criteria) this;
        }

        public Criteria andEncourageWordLessThan(String value) {
            addCriterion("encourage_word <", value, "encourageWord");
            return (Criteria) this;
        }

        public Criteria andEncourageWordLessThanOrEqualTo(String value) {
            addCriterion("encourage_word <=", value, "encourageWord");
            return (Criteria) this;
        }

        public Criteria andEncourageWordLike(String value) {
            addCriterion("encourage_word like", value, "encourageWord");
            return (Criteria) this;
        }

        public Criteria andEncourageWordNotLike(String value) {
            addCriterion("encourage_word not like", value, "encourageWord");
            return (Criteria) this;
        }

        public Criteria andEncourageWordIn(List<String> values) {
            addCriterion("encourage_word in", values, "encourageWord");
            return (Criteria) this;
        }

        public Criteria andEncourageWordNotIn(List<String> values) {
            addCriterion("encourage_word not in", values, "encourageWord");
            return (Criteria) this;
        }

        public Criteria andEncourageWordBetween(String value1, String value2) {
            addCriterion("encourage_word between", value1, value2, "encourageWord");
            return (Criteria) this;
        }

        public Criteria andEncourageWordNotBetween(String value1, String value2) {
            addCriterion("encourage_word not between", value1, value2, "encourageWord");
            return (Criteria) this;
        }

        public Criteria andReadFlagIsNull() {
            addCriterion("read_flag is null");
            return (Criteria) this;
        }

        public Criteria andReadFlagIsNotNull() {
            addCriterion("read_flag is not null");
            return (Criteria) this;
        }

        public Criteria andReadFlagEqualTo(Integer value) {
            addCriterion("read_flag =", value, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagNotEqualTo(Integer value) {
            addCriterion("read_flag <>", value, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagGreaterThan(Integer value) {
            addCriterion("read_flag >", value, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("read_flag >=", value, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagLessThan(Integer value) {
            addCriterion("read_flag <", value, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagLessThanOrEqualTo(Integer value) {
            addCriterion("read_flag <=", value, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagIn(List<Integer> values) {
            addCriterion("read_flag in", values, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagNotIn(List<Integer> values) {
            addCriterion("read_flag not in", values, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagBetween(Integer value1, Integer value2) {
            addCriterion("read_flag between", value1, value2, "readFlag");
            return (Criteria) this;
        }

        public Criteria andReadFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("read_flag not between", value1, value2, "readFlag");
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