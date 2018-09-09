package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DurationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DurationExample() {
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

        public Criteria andValidTimeIsNull() {
            addCriterion("valid_time is null");
            return (Criteria) this;
        }

        public Criteria andValidTimeIsNotNull() {
            addCriterion("valid_time is not null");
            return (Criteria) this;
        }

        public Criteria andValidTimeEqualTo(Long value) {
            addCriterion("valid_time =", value, "validTime");
            return (Criteria) this;
        }

        public Criteria andValidTimeNotEqualTo(Long value) {
            addCriterion("valid_time <>", value, "validTime");
            return (Criteria) this;
        }

        public Criteria andValidTimeGreaterThan(Long value) {
            addCriterion("valid_time >", value, "validTime");
            return (Criteria) this;
        }

        public Criteria andValidTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("valid_time >=", value, "validTime");
            return (Criteria) this;
        }

        public Criteria andValidTimeLessThan(Long value) {
            addCriterion("valid_time <", value, "validTime");
            return (Criteria) this;
        }

        public Criteria andValidTimeLessThanOrEqualTo(Long value) {
            addCriterion("valid_time <=", value, "validTime");
            return (Criteria) this;
        }

        public Criteria andValidTimeIn(List<Long> values) {
            addCriterion("valid_time in", values, "validTime");
            return (Criteria) this;
        }

        public Criteria andValidTimeNotIn(List<Long> values) {
            addCriterion("valid_time not in", values, "validTime");
            return (Criteria) this;
        }

        public Criteria andValidTimeBetween(Long value1, Long value2) {
            addCriterion("valid_time between", value1, value2, "validTime");
            return (Criteria) this;
        }

        public Criteria andValidTimeNotBetween(Long value1, Long value2) {
            addCriterion("valid_time not between", value1, value2, "validTime");
            return (Criteria) this;
        }

        public Criteria andOnlineTimeIsNull() {
            addCriterion("online_time is null");
            return (Criteria) this;
        }

        public Criteria andOnlineTimeIsNotNull() {
            addCriterion("online_time is not null");
            return (Criteria) this;
        }

        public Criteria andOnlineTimeEqualTo(Long value) {
            addCriterion("online_time =", value, "onlineTime");
            return (Criteria) this;
        }

        public Criteria andOnlineTimeNotEqualTo(Long value) {
            addCriterion("online_time <>", value, "onlineTime");
            return (Criteria) this;
        }

        public Criteria andOnlineTimeGreaterThan(Long value) {
            addCriterion("online_time >", value, "onlineTime");
            return (Criteria) this;
        }

        public Criteria andOnlineTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("online_time >=", value, "onlineTime");
            return (Criteria) this;
        }

        public Criteria andOnlineTimeLessThan(Long value) {
            addCriterion("online_time <", value, "onlineTime");
            return (Criteria) this;
        }

        public Criteria andOnlineTimeLessThanOrEqualTo(Long value) {
            addCriterion("online_time <=", value, "onlineTime");
            return (Criteria) this;
        }

        public Criteria andOnlineTimeIn(List<Long> values) {
            addCriterion("online_time in", values, "onlineTime");
            return (Criteria) this;
        }

        public Criteria andOnlineTimeNotIn(List<Long> values) {
            addCriterion("online_time not in", values, "onlineTime");
            return (Criteria) this;
        }

        public Criteria andOnlineTimeBetween(Long value1, Long value2) {
            addCriterion("online_time between", value1, value2, "onlineTime");
            return (Criteria) this;
        }

        public Criteria andOnlineTimeNotBetween(Long value1, Long value2) {
            addCriterion("online_time not between", value1, value2, "onlineTime");
            return (Criteria) this;
        }

        public Criteria andLoginTimeIsNull() {
            addCriterion("login_time is null");
            return (Criteria) this;
        }

        public Criteria andLoginTimeIsNotNull() {
            addCriterion("login_time is not null");
            return (Criteria) this;
        }

        public Criteria andLoginTimeEqualTo(Date value) {
            addCriterion("login_time =", value, "loginTime");
            return (Criteria) this;
        }

        public Criteria andLoginTimeNotEqualTo(Date value) {
            addCriterion("login_time <>", value, "loginTime");
            return (Criteria) this;
        }

        public Criteria andLoginTimeGreaterThan(Date value) {
            addCriterion("login_time >", value, "loginTime");
            return (Criteria) this;
        }

        public Criteria andLoginTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("login_time >=", value, "loginTime");
            return (Criteria) this;
        }

        public Criteria andLoginTimeLessThan(Date value) {
            addCriterion("login_time <", value, "loginTime");
            return (Criteria) this;
        }

        public Criteria andLoginTimeLessThanOrEqualTo(Date value) {
            addCriterion("login_time <=", value, "loginTime");
            return (Criteria) this;
        }

        public Criteria andLoginTimeIn(List<Date> values) {
            addCriterion("login_time in", values, "loginTime");
            return (Criteria) this;
        }

        public Criteria andLoginTimeNotIn(List<Date> values) {
            addCriterion("login_time not in", values, "loginTime");
            return (Criteria) this;
        }

        public Criteria andLoginTimeBetween(Date value1, Date value2) {
            addCriterion("login_time between", value1, value2, "loginTime");
            return (Criteria) this;
        }

        public Criteria andLoginTimeNotBetween(Date value1, Date value2) {
            addCriterion("login_time not between", value1, value2, "loginTime");
            return (Criteria) this;
        }

        public Criteria andLoginOutTimeIsNull() {
            addCriterion("login_out_time is null");
            return (Criteria) this;
        }

        public Criteria andLoginOutTimeIsNotNull() {
            addCriterion("login_out_time is not null");
            return (Criteria) this;
        }

        public Criteria andLoginOutTimeEqualTo(Date value) {
            addCriterion("login_out_time =", value, "loginOutTime");
            return (Criteria) this;
        }

        public Criteria andLoginOutTimeNotEqualTo(Date value) {
            addCriterion("login_out_time <>", value, "loginOutTime");
            return (Criteria) this;
        }

        public Criteria andLoginOutTimeGreaterThan(Date value) {
            addCriterion("login_out_time >", value, "loginOutTime");
            return (Criteria) this;
        }

        public Criteria andLoginOutTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("login_out_time >=", value, "loginOutTime");
            return (Criteria) this;
        }

        public Criteria andLoginOutTimeLessThan(Date value) {
            addCriterion("login_out_time <", value, "loginOutTime");
            return (Criteria) this;
        }

        public Criteria andLoginOutTimeLessThanOrEqualTo(Date value) {
            addCriterion("login_out_time <=", value, "loginOutTime");
            return (Criteria) this;
        }

        public Criteria andLoginOutTimeIn(List<Date> values) {
            addCriterion("login_out_time in", values, "loginOutTime");
            return (Criteria) this;
        }

        public Criteria andLoginOutTimeNotIn(List<Date> values) {
            addCriterion("login_out_time not in", values, "loginOutTime");
            return (Criteria) this;
        }

        public Criteria andLoginOutTimeBetween(Date value1, Date value2) {
            addCriterion("login_out_time between", value1, value2, "loginOutTime");
            return (Criteria) this;
        }

        public Criteria andLoginOutTimeNotBetween(Date value1, Date value2) {
            addCriterion("login_out_time not between", value1, value2, "loginOutTime");
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