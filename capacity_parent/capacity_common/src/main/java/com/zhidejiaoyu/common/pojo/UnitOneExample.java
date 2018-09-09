package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 单元查询条件
 *
 * @author wuchenxi
 * @date 2018年5月4日
 */
public class UnitOneExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UnitOneExample() {
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

        public Criteria andJointNameIsNull() {
            addCriterion("joint_name is null");
            return (Criteria) this;
        }

        public Criteria andJointNameIsNotNull() {
            addCriterion("joint_name is not null");
            return (Criteria) this;
        }

        public Criteria andJointNameEqualTo(String value) {
            addCriterion("joint_name =", value, "jointName");
            return (Criteria) this;
        }

        public Criteria andJointNameNotEqualTo(String value) {
            addCriterion("joint_name <>", value, "jointName");
            return (Criteria) this;
        }

        public Criteria andJointNameGreaterThan(String value) {
            addCriterion("joint_name >", value, "jointName");
            return (Criteria) this;
        }

        public Criteria andJointNameGreaterThanOrEqualTo(String value) {
            addCriterion("joint_name >=", value, "jointName");
            return (Criteria) this;
        }

        public Criteria andJointNameLessThan(String value) {
            addCriterion("joint_name <", value, "jointName");
            return (Criteria) this;
        }

        public Criteria andJointNameLessThanOrEqualTo(String value) {
            addCriterion("joint_name <=", value, "jointName");
            return (Criteria) this;
        }

        public Criteria andJointNameLike(String value) {
            addCriterion("joint_name like", value, "jointName");
            return (Criteria) this;
        }

        public Criteria andJointNameNotLike(String value) {
            addCriterion("joint_name not like", value, "jointName");
            return (Criteria) this;
        }

        public Criteria andJointNameIn(List<String> values) {
            addCriterion("joint_name in", values, "jointName");
            return (Criteria) this;
        }

        public Criteria andJointNameNotIn(List<String> values) {
            addCriterion("joint_name not in", values, "jointName");
            return (Criteria) this;
        }

        public Criteria andJointNameBetween(String value1, String value2) {
            addCriterion("joint_name between", value1, value2, "jointName");
            return (Criteria) this;
        }

        public Criteria andJointNameNotBetween(String value1, String value2) {
            addCriterion("joint_name not between", value1, value2, "jointName");
            return (Criteria) this;
        }

        public Criteria andDelstatusIsNull() {
            addCriterion("delStatus is null");
            return (Criteria) this;
        }

        public Criteria andDelstatusIsNotNull() {
            addCriterion("delStatus is not null");
            return (Criteria) this;
        }

        public Criteria andDelstatusEqualTo(Integer value) {
            addCriterion("delStatus =", value, "delstatus");
            return (Criteria) this;
        }

        public Criteria andDelstatusNotEqualTo(Integer value) {
            addCriterion("delStatus <>", value, "delstatus");
            return (Criteria) this;
        }

        public Criteria andDelstatusGreaterThan(Integer value) {
            addCriterion("delStatus >", value, "delstatus");
            return (Criteria) this;
        }

        public Criteria andDelstatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("delStatus >=", value, "delstatus");
            return (Criteria) this;
        }

        public Criteria andDelstatusLessThan(Integer value) {
            addCriterion("delStatus <", value, "delstatus");
            return (Criteria) this;
        }

        public Criteria andDelstatusLessThanOrEqualTo(Integer value) {
            addCriterion("delStatus <=", value, "delstatus");
            return (Criteria) this;
        }

        public Criteria andDelstatusIn(List<Integer> values) {
            addCriterion("delStatus in", values, "delstatus");
            return (Criteria) this;
        }

        public Criteria andDelstatusNotIn(List<Integer> values) {
            addCriterion("delStatus not in", values, "delstatus");
            return (Criteria) this;
        }

        public Criteria andDelstatusBetween(Integer value1, Integer value2) {
            addCriterion("delStatus between", value1, value2, "delstatus");
            return (Criteria) this;
        }

        public Criteria andDelstatusNotBetween(Integer value1, Integer value2) {
            addCriterion("delStatus not between", value1, value2, "delstatus");
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