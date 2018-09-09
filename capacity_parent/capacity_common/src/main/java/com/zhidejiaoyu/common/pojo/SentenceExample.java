package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class SentenceExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SentenceExample() {
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

        public Criteria andSentence1IsNull() {
            addCriterion("sentence1 is null");
            return (Criteria) this;
        }

        public Criteria andSentence1IsNotNull() {
            addCriterion("sentence1 is not null");
            return (Criteria) this;
        }

        public Criteria andSentence1EqualTo(String value) {
            addCriterion("sentence1 =", value, "sentence1");
            return (Criteria) this;
        }

        public Criteria andSentence1NotEqualTo(String value) {
            addCriterion("sentence1 <>", value, "sentence1");
            return (Criteria) this;
        }

        public Criteria andSentence1GreaterThan(String value) {
            addCriterion("sentence1 >", value, "sentence1");
            return (Criteria) this;
        }

        public Criteria andSentence1GreaterThanOrEqualTo(String value) {
            addCriterion("sentence1 >=", value, "sentence1");
            return (Criteria) this;
        }

        public Criteria andSentence1LessThan(String value) {
            addCriterion("sentence1 <", value, "sentence1");
            return (Criteria) this;
        }

        public Criteria andSentence1LessThanOrEqualTo(String value) {
            addCriterion("sentence1 <=", value, "sentence1");
            return (Criteria) this;
        }

        public Criteria andSentence1Like(String value) {
            addCriterion("sentence1 like", value, "sentence1");
            return (Criteria) this;
        }

        public Criteria andSentence1NotLike(String value) {
            addCriterion("sentence1 not like", value, "sentence1");
            return (Criteria) this;
        }

        public Criteria andSentence1In(List<String> values) {
            addCriterion("sentence1 in", values, "sentence1");
            return (Criteria) this;
        }

        public Criteria andSentence1NotIn(List<String> values) {
            addCriterion("sentence1 not in", values, "sentence1");
            return (Criteria) this;
        }

        public Criteria andSentence1Between(String value1, String value2) {
            addCriterion("sentence1 between", value1, value2, "sentence1");
            return (Criteria) this;
        }

        public Criteria andSentence1NotBetween(String value1, String value2) {
            addCriterion("sentence1 not between", value1, value2, "sentence1");
            return (Criteria) this;
        }

        public Criteria andSentence2IsNull() {
            addCriterion("sentence2 is null");
            return (Criteria) this;
        }

        public Criteria andSentence2IsNotNull() {
            addCriterion("sentence2 is not null");
            return (Criteria) this;
        }

        public Criteria andSentence2EqualTo(String value) {
            addCriterion("sentence2 =", value, "sentence2");
            return (Criteria) this;
        }

        public Criteria andSentence2NotEqualTo(String value) {
            addCriterion("sentence2 <>", value, "sentence2");
            return (Criteria) this;
        }

        public Criteria andSentence2GreaterThan(String value) {
            addCriterion("sentence2 >", value, "sentence2");
            return (Criteria) this;
        }

        public Criteria andSentence2GreaterThanOrEqualTo(String value) {
            addCriterion("sentence2 >=", value, "sentence2");
            return (Criteria) this;
        }

        public Criteria andSentence2LessThan(String value) {
            addCriterion("sentence2 <", value, "sentence2");
            return (Criteria) this;
        }

        public Criteria andSentence2LessThanOrEqualTo(String value) {
            addCriterion("sentence2 <=", value, "sentence2");
            return (Criteria) this;
        }

        public Criteria andSentence2Like(String value) {
            addCriterion("sentence2 like", value, "sentence2");
            return (Criteria) this;
        }

        public Criteria andSentence2NotLike(String value) {
            addCriterion("sentence2 not like", value, "sentence2");
            return (Criteria) this;
        }

        public Criteria andSentence2In(List<String> values) {
            addCriterion("sentence2 in", values, "sentence2");
            return (Criteria) this;
        }

        public Criteria andSentence2NotIn(List<String> values) {
            addCriterion("sentence2 not in", values, "sentence2");
            return (Criteria) this;
        }

        public Criteria andSentence2Between(String value1, String value2) {
            addCriterion("sentence2 between", value1, value2, "sentence2");
            return (Criteria) this;
        }

        public Criteria andSentence2NotBetween(String value1, String value2) {
            addCriterion("sentence2 not between", value1, value2, "sentence2");
            return (Criteria) this;
        }

        public Criteria andSentence3IsNull() {
            addCriterion("sentence3 is null");
            return (Criteria) this;
        }

        public Criteria andSentence3IsNotNull() {
            addCriterion("sentence3 is not null");
            return (Criteria) this;
        }

        public Criteria andSentence3EqualTo(String value) {
            addCriterion("sentence3 =", value, "sentence3");
            return (Criteria) this;
        }

        public Criteria andSentence3NotEqualTo(String value) {
            addCriterion("sentence3 <>", value, "sentence3");
            return (Criteria) this;
        }

        public Criteria andSentence3GreaterThan(String value) {
            addCriterion("sentence3 >", value, "sentence3");
            return (Criteria) this;
        }

        public Criteria andSentence3GreaterThanOrEqualTo(String value) {
            addCriterion("sentence3 >=", value, "sentence3");
            return (Criteria) this;
        }

        public Criteria andSentence3LessThan(String value) {
            addCriterion("sentence3 <", value, "sentence3");
            return (Criteria) this;
        }

        public Criteria andSentence3LessThanOrEqualTo(String value) {
            addCriterion("sentence3 <=", value, "sentence3");
            return (Criteria) this;
        }

        public Criteria andSentence3Like(String value) {
            addCriterion("sentence3 like", value, "sentence3");
            return (Criteria) this;
        }

        public Criteria andSentence3NotLike(String value) {
            addCriterion("sentence3 not like", value, "sentence3");
            return (Criteria) this;
        }

        public Criteria andSentence3In(List<String> values) {
            addCriterion("sentence3 in", values, "sentence3");
            return (Criteria) this;
        }

        public Criteria andSentence3NotIn(List<String> values) {
            addCriterion("sentence3 not in", values, "sentence3");
            return (Criteria) this;
        }

        public Criteria andSentence3Between(String value1, String value2) {
            addCriterion("sentence3 between", value1, value2, "sentence3");
            return (Criteria) this;
        }

        public Criteria andSentence3NotBetween(String value1, String value2) {
            addCriterion("sentence3 not between", value1, value2, "sentence3");
            return (Criteria) this;
        }

        public Criteria andSentence4IsNull() {
            addCriterion("sentence4 is null");
            return (Criteria) this;
        }

        public Criteria andSentence4IsNotNull() {
            addCriterion("sentence4 is not null");
            return (Criteria) this;
        }

        public Criteria andSentence4EqualTo(String value) {
            addCriterion("sentence4 =", value, "sentence4");
            return (Criteria) this;
        }

        public Criteria andSentence4NotEqualTo(String value) {
            addCriterion("sentence4 <>", value, "sentence4");
            return (Criteria) this;
        }

        public Criteria andSentence4GreaterThan(String value) {
            addCriterion("sentence4 >", value, "sentence4");
            return (Criteria) this;
        }

        public Criteria andSentence4GreaterThanOrEqualTo(String value) {
            addCriterion("sentence4 >=", value, "sentence4");
            return (Criteria) this;
        }

        public Criteria andSentence4LessThan(String value) {
            addCriterion("sentence4 <", value, "sentence4");
            return (Criteria) this;
        }

        public Criteria andSentence4LessThanOrEqualTo(String value) {
            addCriterion("sentence4 <=", value, "sentence4");
            return (Criteria) this;
        }

        public Criteria andSentence4Like(String value) {
            addCriterion("sentence4 like", value, "sentence4");
            return (Criteria) this;
        }

        public Criteria andSentence4NotLike(String value) {
            addCriterion("sentence4 not like", value, "sentence4");
            return (Criteria) this;
        }

        public Criteria andSentence4In(List<String> values) {
            addCriterion("sentence4 in", values, "sentence4");
            return (Criteria) this;
        }

        public Criteria andSentence4NotIn(List<String> values) {
            addCriterion("sentence4 not in", values, "sentence4");
            return (Criteria) this;
        }

        public Criteria andSentence4Between(String value1, String value2) {
            addCriterion("sentence4 between", value1, value2, "sentence4");
            return (Criteria) this;
        }

        public Criteria andSentence4NotBetween(String value1, String value2) {
            addCriterion("sentence4 not between", value1, value2, "sentence4");
            return (Criteria) this;
        }

        public Criteria andSentence5IsNull() {
            addCriterion("sentence5 is null");
            return (Criteria) this;
        }

        public Criteria andSentence5IsNotNull() {
            addCriterion("sentence5 is not null");
            return (Criteria) this;
        }

        public Criteria andSentence5EqualTo(String value) {
            addCriterion("sentence5 =", value, "sentence5");
            return (Criteria) this;
        }

        public Criteria andSentence5NotEqualTo(String value) {
            addCriterion("sentence5 <>", value, "sentence5");
            return (Criteria) this;
        }

        public Criteria andSentence5GreaterThan(String value) {
            addCriterion("sentence5 >", value, "sentence5");
            return (Criteria) this;
        }

        public Criteria andSentence5GreaterThanOrEqualTo(String value) {
            addCriterion("sentence5 >=", value, "sentence5");
            return (Criteria) this;
        }

        public Criteria andSentence5LessThan(String value) {
            addCriterion("sentence5 <", value, "sentence5");
            return (Criteria) this;
        }

        public Criteria andSentence5LessThanOrEqualTo(String value) {
            addCriterion("sentence5 <=", value, "sentence5");
            return (Criteria) this;
        }

        public Criteria andSentence5Like(String value) {
            addCriterion("sentence5 like", value, "sentence5");
            return (Criteria) this;
        }

        public Criteria andSentence5NotLike(String value) {
            addCriterion("sentence5 not like", value, "sentence5");
            return (Criteria) this;
        }

        public Criteria andSentence5In(List<String> values) {
            addCriterion("sentence5 in", values, "sentence5");
            return (Criteria) this;
        }

        public Criteria andSentence5NotIn(List<String> values) {
            addCriterion("sentence5 not in", values, "sentence5");
            return (Criteria) this;
        }

        public Criteria andSentence5Between(String value1, String value2) {
            addCriterion("sentence5 between", value1, value2, "sentence5");
            return (Criteria) this;
        }

        public Criteria andSentence5NotBetween(String value1, String value2) {
            addCriterion("sentence5 not between", value1, value2, "sentence5");
            return (Criteria) this;
        }

        public Criteria andSentence6IsNull() {
            addCriterion("sentence6 is null");
            return (Criteria) this;
        }

        public Criteria andSentence6IsNotNull() {
            addCriterion("sentence6 is not null");
            return (Criteria) this;
        }

        public Criteria andSentence6EqualTo(String value) {
            addCriterion("sentence6 =", value, "sentence6");
            return (Criteria) this;
        }

        public Criteria andSentence6NotEqualTo(String value) {
            addCriterion("sentence6 <>", value, "sentence6");
            return (Criteria) this;
        }

        public Criteria andSentence6GreaterThan(String value) {
            addCriterion("sentence6 >", value, "sentence6");
            return (Criteria) this;
        }

        public Criteria andSentence6GreaterThanOrEqualTo(String value) {
            addCriterion("sentence6 >=", value, "sentence6");
            return (Criteria) this;
        }

        public Criteria andSentence6LessThan(String value) {
            addCriterion("sentence6 <", value, "sentence6");
            return (Criteria) this;
        }

        public Criteria andSentence6LessThanOrEqualTo(String value) {
            addCriterion("sentence6 <=", value, "sentence6");
            return (Criteria) this;
        }

        public Criteria andSentence6Like(String value) {
            addCriterion("sentence6 like", value, "sentence6");
            return (Criteria) this;
        }

        public Criteria andSentence6NotLike(String value) {
            addCriterion("sentence6 not like", value, "sentence6");
            return (Criteria) this;
        }

        public Criteria andSentence6In(List<String> values) {
            addCriterion("sentence6 in", values, "sentence6");
            return (Criteria) this;
        }

        public Criteria andSentence6NotIn(List<String> values) {
            addCriterion("sentence6 not in", values, "sentence6");
            return (Criteria) this;
        }

        public Criteria andSentence6Between(String value1, String value2) {
            addCriterion("sentence6 between", value1, value2, "sentence6");
            return (Criteria) this;
        }

        public Criteria andSentence6NotBetween(String value1, String value2) {
            addCriterion("sentence6 not between", value1, value2, "sentence6");
            return (Criteria) this;
        }

        public Criteria andSentence7IsNull() {
            addCriterion("sentence7 is null");
            return (Criteria) this;
        }

        public Criteria andSentence7IsNotNull() {
            addCriterion("sentence7 is not null");
            return (Criteria) this;
        }

        public Criteria andSentence7EqualTo(String value) {
            addCriterion("sentence7 =", value, "sentence7");
            return (Criteria) this;
        }

        public Criteria andSentence7NotEqualTo(String value) {
            addCriterion("sentence7 <>", value, "sentence7");
            return (Criteria) this;
        }

        public Criteria andSentence7GreaterThan(String value) {
            addCriterion("sentence7 >", value, "sentence7");
            return (Criteria) this;
        }

        public Criteria andSentence7GreaterThanOrEqualTo(String value) {
            addCriterion("sentence7 >=", value, "sentence7");
            return (Criteria) this;
        }

        public Criteria andSentence7LessThan(String value) {
            addCriterion("sentence7 <", value, "sentence7");
            return (Criteria) this;
        }

        public Criteria andSentence7LessThanOrEqualTo(String value) {
            addCriterion("sentence7 <=", value, "sentence7");
            return (Criteria) this;
        }

        public Criteria andSentence7Like(String value) {
            addCriterion("sentence7 like", value, "sentence7");
            return (Criteria) this;
        }

        public Criteria andSentence7NotLike(String value) {
            addCriterion("sentence7 not like", value, "sentence7");
            return (Criteria) this;
        }

        public Criteria andSentence7In(List<String> values) {
            addCriterion("sentence7 in", values, "sentence7");
            return (Criteria) this;
        }

        public Criteria andSentence7NotIn(List<String> values) {
            addCriterion("sentence7 not in", values, "sentence7");
            return (Criteria) this;
        }

        public Criteria andSentence7Between(String value1, String value2) {
            addCriterion("sentence7 between", value1, value2, "sentence7");
            return (Criteria) this;
        }

        public Criteria andSentence7NotBetween(String value1, String value2) {
            addCriterion("sentence7 not between", value1, value2, "sentence7");
            return (Criteria) this;
        }

        public Criteria andSentence8IsNull() {
            addCriterion("sentence8 is null");
            return (Criteria) this;
        }

        public Criteria andSentence8IsNotNull() {
            addCriterion("sentence8 is not null");
            return (Criteria) this;
        }

        public Criteria andSentence8EqualTo(String value) {
            addCriterion("sentence8 =", value, "sentence8");
            return (Criteria) this;
        }

        public Criteria andSentence8NotEqualTo(String value) {
            addCriterion("sentence8 <>", value, "sentence8");
            return (Criteria) this;
        }

        public Criteria andSentence8GreaterThan(String value) {
            addCriterion("sentence8 >", value, "sentence8");
            return (Criteria) this;
        }

        public Criteria andSentence8GreaterThanOrEqualTo(String value) {
            addCriterion("sentence8 >=", value, "sentence8");
            return (Criteria) this;
        }

        public Criteria andSentence8LessThan(String value) {
            addCriterion("sentence8 <", value, "sentence8");
            return (Criteria) this;
        }

        public Criteria andSentence8LessThanOrEqualTo(String value) {
            addCriterion("sentence8 <=", value, "sentence8");
            return (Criteria) this;
        }

        public Criteria andSentence8Like(String value) {
            addCriterion("sentence8 like", value, "sentence8");
            return (Criteria) this;
        }

        public Criteria andSentence8NotLike(String value) {
            addCriterion("sentence8 not like", value, "sentence8");
            return (Criteria) this;
        }

        public Criteria andSentence8In(List<String> values) {
            addCriterion("sentence8 in", values, "sentence8");
            return (Criteria) this;
        }

        public Criteria andSentence8NotIn(List<String> values) {
            addCriterion("sentence8 not in", values, "sentence8");
            return (Criteria) this;
        }

        public Criteria andSentence8Between(String value1, String value2) {
            addCriterion("sentence8 between", value1, value2, "sentence8");
            return (Criteria) this;
        }

        public Criteria andSentence8NotBetween(String value1, String value2) {
            addCriterion("sentence8 not between", value1, value2, "sentence8");
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