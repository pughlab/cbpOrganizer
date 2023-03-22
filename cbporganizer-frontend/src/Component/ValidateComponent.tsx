import React, {useEffect, useState} from 'react'
import useAxios from "../service/useAxios";
import {ProgressSpinner} from "primereact/progressspinner";

const ValidateComponent = () => {

    const { response, loading, error, operation } = useAxios();

    // validation message
    const [validateMessage, setValidateMessage] = useState('');
    // show the html result button
    const [validateResponse, setValidateResponse] = useState<any>();

    useEffect(() => {
        console.log('response: ', response);
        if (response !== null) {
            setValidateMessage("Validation finished");
            setValidateResponse(response);
        }
    }, [response]);

    // check if there's any error
    useEffect(() => {
        if (error !== null) {
            setValidateMessage(error)
        }
    }, [error]);

    const handleValidate = () => {
        operation({
            method: 'GET',
            url: 'validate',
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
            },
        })
    }

    const handleShowURL = () => {
        const file = new Blob([validateResponse], {type: 'text/html'});
        const fileURL = URL.createObjectURL(file);
        window.open(fileURL);
    }

    return (
      <>
          <p>Validate your files with the dataset validator </p>
          <button onClick={handleValidate}>validate</button>
          {loading && <div className="card flex justify-content-center">
              <ProgressSpinner />
          </div>}
          <br/>
          <br/>
          {validateMessage && <p>{validateMessage}</p>}
          <br />
          {validateResponse && (
              <div>
                  <button onClick={handleShowURL}>Open Result</button>
              </div>
          )}
      </>
    );
}

export default ValidateComponent
