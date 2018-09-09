package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class RankListExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public RankListExample() {
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

        public Criteria andSchoolDayRankIsNull() {
            addCriterion("school_day_rank is null");
            return (Criteria) this;
        }

        public Criteria andSchoolDayRankIsNotNull() {
            addCriterion("school_day_rank is not null");
            return (Criteria) this;
        }

        public Criteria andSchoolDayRankEqualTo(Integer value) {
            addCriterion("school_day_rank =", value, "schoolDayRank");
            return (Criteria) this;
        }

        public Criteria andSchoolDayRankNotEqualTo(Integer value) {
            addCriterion("school_day_rank <>", value, "schoolDayRank");
            return (Criteria) this;
        }

        public Criteria andSchoolDayRankGreaterThan(Integer value) {
            addCriterion("school_day_rank >", value, "schoolDayRank");
            return (Criteria) this;
        }

        public Criteria andSchoolDayRankGreaterThanOrEqualTo(Integer value) {
            addCriterion("school_day_rank >=", value, "schoolDayRank");
            return (Criteria) this;
        }

        public Criteria andSchoolDayRankLessThan(Integer value) {
            addCriterion("school_day_rank <", value, "schoolDayRank");
            return (Criteria) this;
        }

        public Criteria andSchoolDayRankLessThanOrEqualTo(Integer value) {
            addCriterion("school_day_rank <=", value, "schoolDayRank");
            return (Criteria) this;
        }

        public Criteria andSchoolDayRankIn(List<Integer> values) {
            addCriterion("school_day_rank in", values, "schoolDayRank");
            return (Criteria) this;
        }

        public Criteria andSchoolDayRankNotIn(List<Integer> values) {
            addCriterion("school_day_rank not in", values, "schoolDayRank");
            return (Criteria) this;
        }

        public Criteria andSchoolDayRankBetween(Integer value1, Integer value2) {
            addCriterion("school_day_rank between", value1, value2, "schoolDayRank");
            return (Criteria) this;
        }

        public Criteria andSchoolDayRankNotBetween(Integer value1, Integer value2) {
            addCriterion("school_day_rank not between", value1, value2, "schoolDayRank");
            return (Criteria) this;
        }

        public Criteria andSchoolWeekRankIsNull() {
            addCriterion("school_week_rank is null");
            return (Criteria) this;
        }

        public Criteria andSchoolWeekRankIsNotNull() {
            addCriterion("school_week_rank is not null");
            return (Criteria) this;
        }

        public Criteria andSchoolWeekRankEqualTo(Integer value) {
            addCriterion("school_week_rank =", value, "schoolWeekRank");
            return (Criteria) this;
        }

        public Criteria andSchoolWeekRankNotEqualTo(Integer value) {
            addCriterion("school_week_rank <>", value, "schoolWeekRank");
            return (Criteria) this;
        }

        public Criteria andSchoolWeekRankGreaterThan(Integer value) {
            addCriterion("school_week_rank >", value, "schoolWeekRank");
            return (Criteria) this;
        }

        public Criteria andSchoolWeekRankGreaterThanOrEqualTo(Integer value) {
            addCriterion("school_week_rank >=", value, "schoolWeekRank");
            return (Criteria) this;
        }

        public Criteria andSchoolWeekRankLessThan(Integer value) {
            addCriterion("school_week_rank <", value, "schoolWeekRank");
            return (Criteria) this;
        }

        public Criteria andSchoolWeekRankLessThanOrEqualTo(Integer value) {
            addCriterion("school_week_rank <=", value, "schoolWeekRank");
            return (Criteria) this;
        }

        public Criteria andSchoolWeekRankIn(List<Integer> values) {
            addCriterion("school_week_rank in", values, "schoolWeekRank");
            return (Criteria) this;
        }

        public Criteria andSchoolWeekRankNotIn(List<Integer> values) {
            addCriterion("school_week_rank not in", values, "schoolWeekRank");
            return (Criteria) this;
        }

        public Criteria andSchoolWeekRankBetween(Integer value1, Integer value2) {
            addCriterion("school_week_rank between", value1, value2, "schoolWeekRank");
            return (Criteria) this;
        }

        public Criteria andSchoolWeekRankNotBetween(Integer value1, Integer value2) {
            addCriterion("school_week_rank not between", value1, value2, "schoolWeekRank");
            return (Criteria) this;
        }

        public Criteria andSchoolMonthRankIsNull() {
            addCriterion("school_month_rank is null");
            return (Criteria) this;
        }

        public Criteria andSchoolMonthRankIsNotNull() {
            addCriterion("school_month_rank is not null");
            return (Criteria) this;
        }

        public Criteria andSchoolMonthRankEqualTo(Integer value) {
            addCriterion("school_month_rank =", value, "schoolMonthRank");
            return (Criteria) this;
        }

        public Criteria andSchoolMonthRankNotEqualTo(Integer value) {
            addCriterion("school_month_rank <>", value, "schoolMonthRank");
            return (Criteria) this;
        }

        public Criteria andSchoolMonthRankGreaterThan(Integer value) {
            addCriterion("school_month_rank >", value, "schoolMonthRank");
            return (Criteria) this;
        }

        public Criteria andSchoolMonthRankGreaterThanOrEqualTo(Integer value) {
            addCriterion("school_month_rank >=", value, "schoolMonthRank");
            return (Criteria) this;
        }

        public Criteria andSchoolMonthRankLessThan(Integer value) {
            addCriterion("school_month_rank <", value, "schoolMonthRank");
            return (Criteria) this;
        }

        public Criteria andSchoolMonthRankLessThanOrEqualTo(Integer value) {
            addCriterion("school_month_rank <=", value, "schoolMonthRank");
            return (Criteria) this;
        }

        public Criteria andSchoolMonthRankIn(List<Integer> values) {
            addCriterion("school_month_rank in", values, "schoolMonthRank");
            return (Criteria) this;
        }

        public Criteria andSchoolMonthRankNotIn(List<Integer> values) {
            addCriterion("school_month_rank not in", values, "schoolMonthRank");
            return (Criteria) this;
        }

        public Criteria andSchoolMonthRankBetween(Integer value1, Integer value2) {
            addCriterion("school_month_rank between", value1, value2, "schoolMonthRank");
            return (Criteria) this;
        }

        public Criteria andSchoolMonthRankNotBetween(Integer value1, Integer value2) {
            addCriterion("school_month_rank not between", value1, value2, "schoolMonthRank");
            return (Criteria) this;
        }

        public Criteria andSchoolLowestRankIsNull() {
            addCriterion("school_lowest_rank is null");
            return (Criteria) this;
        }

        public Criteria andSchoolLowestRankIsNotNull() {
            addCriterion("school_lowest_rank is not null");
            return (Criteria) this;
        }

        public Criteria andSchoolLowestRankEqualTo(Integer value) {
            addCriterion("school_lowest_rank =", value, "schoolLowestRank");
            return (Criteria) this;
        }

        public Criteria andSchoolLowestRankNotEqualTo(Integer value) {
            addCriterion("school_lowest_rank <>", value, "schoolLowestRank");
            return (Criteria) this;
        }

        public Criteria andSchoolLowestRankGreaterThan(Integer value) {
            addCriterion("school_lowest_rank >", value, "schoolLowestRank");
            return (Criteria) this;
        }

        public Criteria andSchoolLowestRankGreaterThanOrEqualTo(Integer value) {
            addCriterion("school_lowest_rank >=", value, "schoolLowestRank");
            return (Criteria) this;
        }

        public Criteria andSchoolLowestRankLessThan(Integer value) {
            addCriterion("school_lowest_rank <", value, "schoolLowestRank");
            return (Criteria) this;
        }

        public Criteria andSchoolLowestRankLessThanOrEqualTo(Integer value) {
            addCriterion("school_lowest_rank <=", value, "schoolLowestRank");
            return (Criteria) this;
        }

        public Criteria andSchoolLowestRankIn(List<Integer> values) {
            addCriterion("school_lowest_rank in", values, "schoolLowestRank");
            return (Criteria) this;
        }

        public Criteria andSchoolLowestRankNotIn(List<Integer> values) {
            addCriterion("school_lowest_rank not in", values, "schoolLowestRank");
            return (Criteria) this;
        }

        public Criteria andSchoolLowestRankBetween(Integer value1, Integer value2) {
            addCriterion("school_lowest_rank between", value1, value2, "schoolLowestRank");
            return (Criteria) this;
        }

        public Criteria andSchoolLowestRankNotBetween(Integer value1, Integer value2) {
            addCriterion("school_lowest_rank not between", value1, value2, "schoolLowestRank");
            return (Criteria) this;
        }

        public Criteria andCountryWeekRankIsNull() {
            addCriterion("country_week_rank is null");
            return (Criteria) this;
        }

        public Criteria andCountryWeekRankIsNotNull() {
            addCriterion("country_week_rank is not null");
            return (Criteria) this;
        }

        public Criteria andCountryWeekRankEqualTo(Integer value) {
            addCriterion("country_week_rank =", value, "countryWeekRank");
            return (Criteria) this;
        }

        public Criteria andCountryWeekRankNotEqualTo(Integer value) {
            addCriterion("country_week_rank <>", value, "countryWeekRank");
            return (Criteria) this;
        }

        public Criteria andCountryWeekRankGreaterThan(Integer value) {
            addCriterion("country_week_rank >", value, "countryWeekRank");
            return (Criteria) this;
        }

        public Criteria andCountryWeekRankGreaterThanOrEqualTo(Integer value) {
            addCriterion("country_week_rank >=", value, "countryWeekRank");
            return (Criteria) this;
        }

        public Criteria andCountryWeekRankLessThan(Integer value) {
            addCriterion("country_week_rank <", value, "countryWeekRank");
            return (Criteria) this;
        }

        public Criteria andCountryWeekRankLessThanOrEqualTo(Integer value) {
            addCriterion("country_week_rank <=", value, "countryWeekRank");
            return (Criteria) this;
        }

        public Criteria andCountryWeekRankIn(List<Integer> values) {
            addCriterion("country_week_rank in", values, "countryWeekRank");
            return (Criteria) this;
        }

        public Criteria andCountryWeekRankNotIn(List<Integer> values) {
            addCriterion("country_week_rank not in", values, "countryWeekRank");
            return (Criteria) this;
        }

        public Criteria andCountryWeekRankBetween(Integer value1, Integer value2) {
            addCriterion("country_week_rank between", value1, value2, "countryWeekRank");
            return (Criteria) this;
        }

        public Criteria andCountryWeekRankNotBetween(Integer value1, Integer value2) {
            addCriterion("country_week_rank not between", value1, value2, "countryWeekRank");
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