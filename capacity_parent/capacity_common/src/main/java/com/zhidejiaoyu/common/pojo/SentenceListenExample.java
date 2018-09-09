package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SentenceListenExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SentenceListenExample() {
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

        public Criteria andVocabularyIdIsNull() {
            addCriterion("vocabulary_id is null");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdIsNotNull() {
            addCriterion("vocabulary_id is not null");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdEqualTo(Long value) {
            addCriterion("vocabulary_id =", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdNotEqualTo(Long value) {
            addCriterion("vocabulary_id <>", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdGreaterThan(Long value) {
            addCriterion("vocabulary_id >", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdGreaterThanOrEqualTo(Long value) {
            addCriterion("vocabulary_id >=", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdLessThan(Long value) {
            addCriterion("vocabulary_id <", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdLessThanOrEqualTo(Long value) {
            addCriterion("vocabulary_id <=", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdIn(List<Long> values) {
            addCriterion("vocabulary_id in", values, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdNotIn(List<Long> values) {
            addCriterion("vocabulary_id not in", values, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdBetween(Long value1, Long value2) {
            addCriterion("vocabulary_id between", value1, value2, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdNotBetween(Long value1, Long value2) {
            addCriterion("vocabulary_id not between", value1, value2, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andWordIsNull() {
            addCriterion("word is null");
            return (Criteria) this;
        }

        public Criteria andWordIsNotNull() {
            addCriterion("word is not null");
            return (Criteria) this;
        }

        public Criteria andWordEqualTo(String value) {
            addCriterion("word =", value, "word");
            return (Criteria) this;
        }

        public Criteria andWordNotEqualTo(String value) {
            addCriterion("word <>", value, "word");
            return (Criteria) this;
        }

        public Criteria andWordGreaterThan(String value) {
            addCriterion("word >", value, "word");
            return (Criteria) this;
        }

        public Criteria andWordGreaterThanOrEqualTo(String value) {
            addCriterion("word >=", value, "word");
            return (Criteria) this;
        }

        public Criteria andWordLessThan(String value) {
            addCriterion("word <", value, "word");
            return (Criteria) this;
        }

        public Criteria andWordLessThanOrEqualTo(String value) {
            addCriterion("word <=", value, "word");
            return (Criteria) this;
        }

        public Criteria andWordLike(String value) {
            addCriterion("word like", value, "word");
            return (Criteria) this;
        }

        public Criteria andWordNotLike(String value) {
            addCriterion("word not like", value, "word");
            return (Criteria) this;
        }

        public Criteria andWordIn(List<String> values) {
            addCriterion("word in", values, "word");
            return (Criteria) this;
        }

        public Criteria andWordNotIn(List<String> values) {
            addCriterion("word not in", values, "word");
            return (Criteria) this;
        }

        public Criteria andWordBetween(String value1, String value2) {
            addCriterion("word between", value1, value2, "word");
            return (Criteria) this;
        }

        public Criteria andWordNotBetween(String value1, String value2) {
            addCriterion("word not between", value1, value2, "word");
            return (Criteria) this;
        }

        public Criteria andWordChineseIsNull() {
            addCriterion("word_chinese is null");
            return (Criteria) this;
        }

        public Criteria andWordChineseIsNotNull() {
            addCriterion("word_chinese is not null");
            return (Criteria) this;
        }

        public Criteria andWordChineseEqualTo(String value) {
            addCriterion("word_chinese =", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseNotEqualTo(String value) {
            addCriterion("word_chinese <>", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseGreaterThan(String value) {
            addCriterion("word_chinese >", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseGreaterThanOrEqualTo(String value) {
            addCriterion("word_chinese >=", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseLessThan(String value) {
            addCriterion("word_chinese <", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseLessThanOrEqualTo(String value) {
            addCriterion("word_chinese <=", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseLike(String value) {
            addCriterion("word_chinese like", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseNotLike(String value) {
            addCriterion("word_chinese not like", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseIn(List<String> values) {
            addCriterion("word_chinese in", values, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseNotIn(List<String> values) {
            addCriterion("word_chinese not in", values, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseBetween(String value1, String value2) {
            addCriterion("word_chinese between", value1, value2, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseNotBetween(String value1, String value2) {
            addCriterion("word_chinese not between", value1, value2, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andFaultTimeIsNull() {
            addCriterion("fault_time is null");
            return (Criteria) this;
        }

        public Criteria andFaultTimeIsNotNull() {
            addCriterion("fault_time is not null");
            return (Criteria) this;
        }

        public Criteria andFaultTimeEqualTo(Integer value) {
            addCriterion("fault_time =", value, "faultTime");
            return (Criteria) this;
        }

        public Criteria andFaultTimeNotEqualTo(Integer value) {
            addCriterion("fault_time <>", value, "faultTime");
            return (Criteria) this;
        }

        public Criteria andFaultTimeGreaterThan(Integer value) {
            addCriterion("fault_time >", value, "faultTime");
            return (Criteria) this;
        }

        public Criteria andFaultTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("fault_time >=", value, "faultTime");
            return (Criteria) this;
        }

        public Criteria andFaultTimeLessThan(Integer value) {
            addCriterion("fault_time <", value, "faultTime");
            return (Criteria) this;
        }

        public Criteria andFaultTimeLessThanOrEqualTo(Integer value) {
            addCriterion("fault_time <=", value, "faultTime");
            return (Criteria) this;
        }

        public Criteria andFaultTimeIn(List<Integer> values) {
            addCriterion("fault_time in", values, "faultTime");
            return (Criteria) this;
        }

        public Criteria andFaultTimeNotIn(List<Integer> values) {
            addCriterion("fault_time not in", values, "faultTime");
            return (Criteria) this;
        }

        public Criteria andFaultTimeBetween(Integer value1, Integer value2) {
            addCriterion("fault_time between", value1, value2, "faultTime");
            return (Criteria) this;
        }

        public Criteria andFaultTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("fault_time not between", value1, value2, "faultTime");
            return (Criteria) this;
        }

        public Criteria andPushIsNull() {
            addCriterion("push is null");
            return (Criteria) this;
        }

        public Criteria andPushIsNotNull() {
            addCriterion("push is not null");
            return (Criteria) this;
        }

        public Criteria andPushEqualTo(Date value) {
            addCriterion("push =", value, "push");
            return (Criteria) this;
        }

        public Criteria andPushNotEqualTo(Date value) {
            addCriterion("push <>", value, "push");
            return (Criteria) this;
        }

        public Criteria andPushGreaterThan(Date value) {
            addCriterion("push >", value, "push");
            return (Criteria) this;
        }

        public Criteria andPushGreaterThanOrEqualTo(Date value) {
            addCriterion("push >=", value, "push");
            return (Criteria) this;
        }

        public Criteria andPushLessThan(Date value) {
            addCriterion("push <", value, "push");
            return (Criteria) this;
        }

        public Criteria andPushLessThanOrEqualTo(Date value) {
            addCriterion("push <=", value, "push");
            return (Criteria) this;
        }

        public Criteria andPushIn(List<Date> values) {
            addCriterion("push in", values, "push");
            return (Criteria) this;
        }

        public Criteria andPushNotIn(List<Date> values) {
            addCriterion("push not in", values, "push");
            return (Criteria) this;
        }

        public Criteria andPushBetween(Date value1, Date value2) {
            addCriterion("push between", value1, value2, "push");
            return (Criteria) this;
        }

        public Criteria andPushNotBetween(Date value1, Date value2) {
            addCriterion("push not between", value1, value2, "push");
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