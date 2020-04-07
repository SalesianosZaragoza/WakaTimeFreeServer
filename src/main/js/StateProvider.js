// store.js
import React, {createContext, useReducer} from 'react';

export const UPDATE_TOKEN = "updateToken";
export const UPDATE_FROM = "updateFrom";
export const UPDATE_TO = "updateTo";

const initialState = {url :"http://localhost:3001/api/"};
const store = createContext(initialState);
const { Provider } = store;

const StateProvider = ( { children } ) => {
  const [state, dispatch] = useReducer((state, action) => {
    switch(action.type) {
      case UPDATE_TOKEN:
        const newStateToken = {...state, tokenId: action.payload.tokenId};
        return newStateToken;
      case UPDATE_FROM:
        const newStateFrom = {...state, from: action.payload.from};
        return newStateFrom;
      case UPDATE_TO:
        const newStateTo = {...state, to: action.payload.to};
        return newStateTo;
      default:
      throw new Error();
    };
  }, initialState);
  return <Provider value={{ state, dispatch }}>{children}</Provider>;
};

export { store, StateProvider }
