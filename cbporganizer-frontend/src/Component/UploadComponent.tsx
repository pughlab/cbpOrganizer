import React, {useEffect, useState} from "react";
import useAxios from "../service/useAxios";
import { ProgressSpinner } from 'primereact/progressspinner';

const UploadComponent = () => {

    const { response, loading, error, operation } = useAxios();
    const { response: fileResponse, loading: fileLoading, error: fileError, operation: fileOperation } = useAxios();

    // status of tar.gz upload
    const [uploadMessage, setUploadMessage] = useState('');
    // list of files extracted from the tar.gz
    const [fileList, setFileList] = useState<any>([]);

    useEffect(() => {
        if (response !== null) {
            setUploadMessage("File uploaded successfully");
            getFiles();
        }
    }, [response]);

    useEffect(() => {
        if (error !== null) {
            setUploadMessage("Failed to upload file");
        }
    }, [error]);

    useEffect(() => {
        if (fileResponse !== null) {
            setFileList(fileResponse);
        }
    }, [fileResponse]);

    useEffect(() => {
        if (fileError !== null) {
            console.log('fileError: ', fileError);
        }
    }, [fileError]);

    const handleFileUpload = (event: any) => {
        const file = event.target.files[0];
        const formData = new FormData();
        formData.append("file", file);

        operation({
            method: 'POST',
            url: 'upload',
            headers: {
                'Content-type': 'multipart/form-data'
            },
            data: formData
        })
    };

    const getFiles = () => {
        fileOperation({
            method: 'GET',
            url: '',
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
            }
        })
    };

    return (
        <>
            <p>Upload a cBioPortal study tar.gz file</p>
            <input type="file" onChange={handleFileUpload} />
            {(loading || fileLoading) && <div className="card flex justify-content-center">
                <ProgressSpinner />
            </div>}
            <br/>
            <br/>
            {uploadMessage && <p>{uploadMessage}</p>}
            {fileList.length > 0 && (
                <div>
                    <h3>Uploaded Files:</h3>
                    <ul>
                        {fileList.map((file: any) => (
                            <li key={file}>{file}</li>
                        ))}
                    </ul>
                </div>
            )}
        </>
    )
}

export default UploadComponent
