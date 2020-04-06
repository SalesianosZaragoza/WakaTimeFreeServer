import PieChart from "../PieChart/main"
import Input from "../Input/main"
import React from 'react';
import { store } from "../../StateProvider"

const Categories = { BRANCH : "branch",  PROJECT : "project", LANGUAGE : "language", FILENAME = "filename"}
Object.freeze(Categories)
const Desktop = (props) => {

    
    const globalState = useContext(store);
    return (
        <>
            <PieChart title={Categories.LANGUAGE} query={store.query(Categories.LANGUAGE)} />
            <PieChart title={Categories.FILENAME} query={store.query(Categories.FILENAME)}/>
            <PieChart title={Categories.BRANCH} query={store.query(Categories.BRANCH)}/>
            <PieChart title={Categories.PROJECT} query={store.query(Categories.PROJECT)}/>
            <Input/>
        </>
        
        );
    }
    export default Desktop;
    