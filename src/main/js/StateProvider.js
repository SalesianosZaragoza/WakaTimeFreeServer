// store.js
import React, {createContext, useReducer} from 'react';

export const UPDATE_TOKEN = "updateToken";
export const UPDATE_FROM = "updateFrom";
export const UPDATE_TO = "updateTo";

export const chartQuery = (params, topic) => {
  var url = new URL("http://localhost:3001/api/Chart/");
  Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))
  return fetch(url) 
};

const initialState = {query: chartQuery};
const store = createContext(initialState);
const { Provider } = store;

const StateProvider = ( { children } ) => {
  const [state, dispatch] = useReducer((state, action) => {
    switch(action.type) {
      case UPDATE_TOKEN:
        const newStateToken = {...state, tokenId: action.payload.tokenId};
        console.log("updatetoken");
        console.log(newStateToken);
        return newStateToken;
      case UPDATE_FROM:
        const newStateFrom = {...state, from: action.payload.from};
        console.log("updatefrom");
        console.log(newStateFrom);
        return newStateFrom;
      case UPDATE_TO:
        const newStateTo = {...state, to: action.payload.to};
        console.log("updateto");
        console.log(newStateTo);
        return newStateTo;
      default:
      throw new Error();
    };
  }, initialState);
  return <Provider value={{ state, dispatch }}>{children}</Provider>;
};

export { store, StateProvider }
