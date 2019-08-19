import axios from 'axios';
import {
  parseHeaderForLinks,
  loadMoreDataWhenScrolled,
  ICrudGetAction,
  ICrudGetAllAction,
  ICrudPutAction,
  ICrudDeleteAction
} from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IRiddle, defaultValue } from 'app/shared/model/riddle.model';

export const ACTION_TYPES = {
  FETCH_RIDDLE_LIST: 'riddle/FETCH_RIDDLE_LIST',
  FETCH_RIDDLE: 'riddle/FETCH_RIDDLE',
  CREATE_RIDDLE: 'riddle/CREATE_RIDDLE',
  UPDATE_RIDDLE: 'riddle/UPDATE_RIDDLE',
  DELETE_RIDDLE: 'riddle/DELETE_RIDDLE',
  RESET: 'riddle/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IRiddle>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type RiddleState = Readonly<typeof initialState>;

// Reducer

export default (state: RiddleState = initialState, action): RiddleState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_RIDDLE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_RIDDLE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_RIDDLE):
    case REQUEST(ACTION_TYPES.UPDATE_RIDDLE):
    case REQUEST(ACTION_TYPES.DELETE_RIDDLE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_RIDDLE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_RIDDLE):
    case FAILURE(ACTION_TYPES.CREATE_RIDDLE):
    case FAILURE(ACTION_TYPES.UPDATE_RIDDLE):
    case FAILURE(ACTION_TYPES.DELETE_RIDDLE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_RIDDLE_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);

      return {
        ...state,
        loading: false,
        links,
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links),
        totalItems: parseInt(action.payload.headers['x-total-count'], 10)
      };
    case SUCCESS(ACTION_TYPES.FETCH_RIDDLE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_RIDDLE):
    case SUCCESS(ACTION_TYPES.UPDATE_RIDDLE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_RIDDLE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/riddles';

// Actions

export const getEntities: ICrudGetAllAction<IRiddle> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_RIDDLE_LIST,
    payload: axios.get<IRiddle>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IRiddle> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_RIDDLE,
    payload: axios.get<IRiddle>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IRiddle> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_RIDDLE,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<IRiddle> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_RIDDLE,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IRiddle> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_RIDDLE,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
